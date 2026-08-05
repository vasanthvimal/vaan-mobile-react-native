import { aiProxyUrl, geminiApiKey, isSecureHttpsUrl } from './config';
import { postJson } from './http';

/**
 * Port of `app/src/main/java/com/example/data/GeminiHelper.kt`.
 *
 * Behavioural parity:
 *  - identical model endpoint, temperatures (0.3 drafts / 0.6 chat),
 *    system-instruction placement and history-alternation rules;
 *  - identical offline fallback copy, so the UX when no key is configured is
 *    character-for-character the same as the Kotlin app.
 *
 * Security improvements over the original (see docs/SECURITY_ASSESSMENT.md):
 *  - the API key travels in the `x-goog-api-key` header instead of the query
 *    string, so it cannot leak through URL logging or crash breadcrumbs;
 *  - an optional first-party proxy (`VAAN_AI_PROXY_URL`) keeps the key off the
 *    device entirely;
 *  - failures are logged without the request payload or key.
 */

const MODEL_ENDPOINT =
  'https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent';

/** OkHttp client used 30s connect/read/write timeouts. */
const REQUEST_TIMEOUT_MS = 30_000;

export type ChatTurn = { sender: string; text: string };

type GeminiPart = { text?: string };
type GeminiResponse = {
  candidates?: { content?: { parts?: GeminiPart[] } }[];
};

type GenerateArgs = {
  prompt: string;
  systemInstruction?: string;
  history?: { role: 'user' | 'model'; text: string }[];
  temperature: number;
  signal?: AbortSignal;
};

const CHAT_SYSTEM_INSTRUCTION = `You are VaanAI, a helpful, highly professional, and friendly AI chatbot assistant for VAAN Consulting (https://www.vaanconsulting.com/).
VAAN Consulting is an independent IT consultancy led by Vasanth N — a technical leader with 16+ years of experience architecting cloud-native data platforms and digital applications for banking, energy, and automotive enterprises.
We specialize in Strategy through to Production across:
- Data Platform Architecture & Strategy: Snowflake, Databricks, Microsoft Fabric, DB2/Oracle database modernizations to PostgreSQL.
- Cloud & Digital Transformation: AWS, Azure, GCP, Kubernetes, multi-cloud strategy, infrastructure as code, cloud cost optimization.
- Mobile Application Development: Secure, offline-first mobile apps, Jetpack Compose UI, local Room DB encryption, WorkManager sync.
- Web Application Development: High-performance single page apps (SPAs), secure API gateways, enterprise SSO integration, CI/CD.
- Bespoke Technology Consulting / Other: SAFe 6 leadership, agile coaching, Scrum, technical audits, mentorship, architecture due diligence.

Key metrics of VAAN Consulting:
- 16+ years delivering cloud & data platforms in regulated enterprises.
- 3 primary sectors: banking, energy & utilities, automotive.
- 30% typical pipeline processing-time reduction delivered.
- 28 professional certifications across cloud, data & security.

Be extremely encouraging, concise, informative, and professional. Always offer to help the user book a consultation in the Bookings tab, submit an inquiry in the Inquiries tab, or view our services. Do not use markdown titles or bold headings excessively, keep text fluid.`;

function extractText(response: GeminiResponse): string | null {
  const parts = response.candidates?.[0]?.content?.parts;
  const text = parts?.[0]?.text;
  return typeof text === 'string' && text.length > 0 ? text : null;
}

/** Never logs prompts, keys, or response bodies. */
function logFailure(scope: string, error: unknown): void {
  const reason = error instanceof Error ? error.name : 'UnknownError';
  console.warn(`[GeminiService] ${scope} failed (${reason}); using offline fallback.`);
}

async function callModel({
  prompt,
  systemInstruction,
  history = [],
  temperature,
  signal,
}: GenerateArgs): Promise<string | null> {
  const contents = [
    ...history.map((turn) => ({ role: turn.role, parts: [{ text: turn.text }] })),
    { role: 'user' as const, parts: [{ text: prompt }] },
  ];

  const payload = {
    contents,
    ...(systemInstruction ? { systemInstruction: { parts: [{ text: systemInstruction }] } } : {}),
    generationConfig: { temperature },
  };

  // Preferred path: first-party proxy holds the credential.
  if (aiProxyUrl) {
    if (!isSecureHttpsUrl(aiProxyUrl)) {
      logFailure('proxy configuration', new Error('InsecureProxyUrl'));
      return null;
    }
    const proxied = await postJson<{ text?: string } & GeminiResponse>({
      url: aiProxyUrl,
      body: payload,
      timeoutMs: REQUEST_TIMEOUT_MS,
      signal,
    });
    return proxied?.text ?? extractText(proxied ?? {});
  }

  if (!geminiApiKey) return null;

  const response = await postJson<GeminiResponse>({
    url: MODEL_ENDPOINT,
    body: payload,
    timeoutMs: REQUEST_TIMEOUT_MS,
    headers: { 'x-goog-api-key': geminiApiKey },
    signal,
  });

  return extractText(response ?? {});
}

// ---------------------------------------------------------------------------
// Draft generation (AI email drafting flow)
// ---------------------------------------------------------------------------

export async function generateDraft(
  prompt: string,
  systemInstruction?: string,
  signal?: AbortSignal,
): Promise<string> {
  try {
    const text = await callModel({ prompt, systemInstruction, temperature: 0.3, signal });
    return text ?? getLocalFallback(prompt);
  } catch (error) {
    logFailure('generateDraft', error);
    return getLocalFallback(prompt);
  }
}

// ---------------------------------------------------------------------------
// Chat (VaanAI assistant)
// ---------------------------------------------------------------------------

/**
 * Reproduces the Kotlin history sanitisation exactly: coerce senders to
 * user/model, keep only strictly alternating turns starting with "user", drop a
 * trailing "user" turn (the live message occupies that slot), then keep the last
 * ten turns.
 */
export function buildChatHistory(chatHistory: ChatTurn[]): { role: 'user' | 'model'; text: string }[] {
  const processed: { role: 'user' | 'model'; text: string }[] = [];
  let expectedRole: 'user' | 'model' = 'user';

  for (const turn of chatHistory) {
    const role: 'user' | 'model' = turn.sender === 'user' ? 'user' : 'model';
    if (role === expectedRole) {
      processed.push({ role, text: turn.text });
      expectedRole = role === 'user' ? 'model' : 'user';
    }
  }

  if (processed.length > 0 && processed[processed.length - 1]!.role === 'user') {
    processed.pop();
  }

  return processed.slice(-10);
}

export async function getChatResponse(
  message: string,
  chatHistory: ChatTurn[],
  signal?: AbortSignal,
): Promise<string> {
  try {
    const text = await callModel({
      prompt: message,
      systemInstruction: CHAT_SYSTEM_INSTRUCTION,
      history: buildChatHistory(chatHistory),
      temperature: 0.6,
      signal,
    });
    return text ?? getChatFallback(message);
  } catch (error) {
    logFailure('getChatResponse', error);
    return getChatFallback(message);
  }
}

// ---------------------------------------------------------------------------
// Offline fallbacks — copy is reproduced verbatim from GeminiHelper.kt
// ---------------------------------------------------------------------------

function includes(haystack: string, needle: string): boolean {
  return haystack.toLowerCase().includes(needle.toLowerCase());
}

export function extractName(prompt: string): string {
  const patterns = ['client named', 'named', 'client', 'Client:'];
  for (const pattern of patterns) {
    const idx = prompt.indexOf(pattern);
    if (idx !== -1) {
      const sub = prompt.substring(idx + pattern.length).trim();
      const word = sub.split(/[ \n,.]/)[0] ?? '';
      if (word.length > 2) return word;
    }
  }
  return 'Valued Client';
}

export function extractSubject(prompt: string): string {
  const idx = prompt.toLowerCase().indexOf('subject:');
  if (idx !== -1) {
    const sub = prompt.substring(idx + 8).trim();
    return sub.split('\n')[0]?.trim() || 'Technical Requirements';
  }
  return 'Your IT Consulting Inquiry';
}

export function extractService(prompt: string): string {
  const services = [
    'Data Platform Architecture & Strategy',
    'Cloud & Digital Transformation',
    'Mobile Application Development',
    'Web Application Development',
    'Bespoke Technology Consulting / Other',
  ];
  for (const service of services) {
    if (includes(prompt, service)) return service;
  }
  return 'IT Consulting Discovery';
}

export function getLocalFallback(prompt: string): string {
  if (includes(prompt, 'inquiry') || includes(prompt, 'reply')) {
    const clientName = extractName(prompt);
    const subject = extractSubject(prompt);
    return `Dear ${clientName},

Thank you for reaching out to Vaan Consulting regarding your interest in "${subject}".

We appreciate you taking the time to share your inquiry with us. As an IT consulting partner specializing in digital transformation, software engineering, and cloud cloud enablement, we would love to learn more about your specific needs and timeline.

One of our senior consultants will review your inquiry details within the next business hour. To help us accelerate the process, we have provisionally saved an open consultation slot for you.

Would you be available for a brief 15-minute alignment call tomorrow or later this week? You can view, confirm or schedule this directly in our mobile dashboard.

Best regards,

The Consulting Team
Vaan Consulting
https://www.vaanconsulting.com/`;
  }

  if (includes(prompt, 'appointment') || includes(prompt, 'confirm')) {
    const clientName = extractName(prompt);
    const service = extractService(prompt);
    return `Subject: Confirmed: Vaan Consulting Technical Discovery - ${service}

Dear ${clientName},

This email is to confirm your upcoming technical consultation with Vaan Consulting.

Service: ${service}
Status: Confirmed & Logged
Video Link: Included in your mobile app calendar

We have assigned one of our principal systems engineers to your account to review your inquiry details beforehand. During this session, we will deep-dive into your architectural goals and deliver an initial scope recommendation.

If you need to reschedule or share any technical briefs beforehand, please reply directly or update your status in our client mobile application.

We look forward to collaborating with you!

Warm regards,

Operations Support
Vaan Consulting
https://www.vaanconsulting.com/`;
  }

  return `Subject: Scheduled Meeting Update - Vaan Consulting

Dear Client,

This is an automated notification from Vaan Consulting regarding our scheduled discussion.

We have synchronized this meeting to our client system, and a notification has been queued for dispatch to your registered address.

Details can be viewed at any time inside the Vaan Consulting dashboard.

Best regards,
Vaan Consulting
https://www.vaanconsulting.com/`;
}

export function getChatFallback(msg: string): string {
  const lower = msg.toLowerCase();
  const has = (...needles: string[]) => needles.some((n) => lower.includes(n));

  if (has('aws', 'gcp', 'azure', 'cloud')) {
    return "Vaan Consulting specializes in enterprise-grade multi-cloud architectures. We design secure, scalable, and resilient platforms on AWS, Google Cloud, and Azure. Vasanth N has over 16 years of experience architecting cloud backends for regulated banking and energy firms. Would you like to check our Services tab or schedule a cloud migration consultation with us?";
  }
  if (has('data', 'snowflake', 'databricks', 'fabric')) {
    return 'Data is engineered to move! We design low-latency, real-time data pipelines and analytics warehouses on Snowflake, Databricks, and Microsoft Fabric. Our clients typically achieve a 30% reduction in data pipeline processing times. If you have legacy database systems, we also consult on zero-downtime migrations to modern engines like PostgreSQL.';
  }
  if (has('mobile', 'android', 'ios', 'app', 'web')) {
    return 'Vaan Consulting builds custom, high-performance mobile and web solutions. Our mobile apps are engineered for offline-first resilience using Jetpack Compose and local encrypted SQLite/Room storage with automated background sync. For web applications, we deploy scalable React/TypeScript portals and low-latency API layers designed to handle substantial user loads.';
  }
  if (has('agility', 'safe', 'agile', 'scrum')) {
    return 'Lead architect Vasanth N is a certified SAFe 6 Agilist with more than 16 years of hands-on experience guiding teams. We help enterprises scale their agile practices, streamline scrum frameworks, and bridge the gap between engineering teams and business leadership.';
  }
  if (has('contact', 'email', 'phone', 'whatsapp', 'address')) {
    return 'You can reach Vaan Consulting in several ways: \n- Email: info@vaanconsulting.com\n- Phone / Mobile: +64 21 000 0000 (New Zealand office)\n- WhatsApp: Chat with us instantly\n- Website: https://www.vaanconsulting.com/\n\nAll of these quick contact methods and direct deep-links are readily accessible in our Contact section inside the Bookings tab!';
  }
  if (has('book', 'consult', 'appointment', 'call', 'schedule')) {
    return 'Booking a Discovery Call with Vaan is incredibly simple! Head over to our Bookings tab right inside this app, fill out your company email, proposed date, and select from our primary consulting services (including Cloud & Digital, Data Platform, or Mobile/Web Dev). It will immediately trigger our scheduling system!';
  }
  if (has('about', 'who', 'vasanth', 'nz', 'zealand')) {
    return 'Vaan Consulting is an independent IT consultancy led by Vasanth N, headquartered in New Zealand with global consulting reach. Vasanth N has over 16 years of technical leadership across banking, energy, and automotive sectors, backed by 28 professional cloud and data certifications. Our engineering mantra is building enterprise platforms that are robust, secure, and engineered to move.';
  }
  if (has('certification', 'cert')) {
    return 'Our team holds 28 professional certifications across major cloud, database, and security providers including AWS Professional, Azure Solutions Architect, Google Professional Cloud Architect, Snowflake, Databricks, and SAFe 6 Agilist.';
  }
  if (has('ai', 'chatbot', 'gemini', 'model')) {
    return 'AI is a core part of our digital transformation advisory. We build data-ingestion pipelines that leverage large language models (like Gemini) to automate support categorization, draft client responses, and extract metadata from complex PDFs. In fact, this chatbot you are talking to is powered by Gemini!';
  }
  return "Hello! I am VaanAI, your virtual consultation assistant. I can answer questions about Vaan Consulting's capabilities in Cloud & Digital Transformation, Data Platform Architecture, Mobile/Web Development, and Bespoke Tech Consulting. Would you like to check our list of services, read some tech insights, or book a free discovery call with us?";
}
