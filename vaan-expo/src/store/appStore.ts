import { create } from 'zustand';

import type { AppDao } from '@/data/dao';
import { InMemoryAppDao } from '@/data/inMemoryDao';
import { AppRepository, emptySnapshot } from '@/data/repository';
import { seedInitialDataIfNecessary } from '@/data/seed';
import { createSqliteDao } from '@/data/sqliteDao';
import type {
  AiDraftState,
  Appointment,
  ChatMessage,
  ClientInquiry,
  ClientMeeting,
  EmailLog,
} from '@/data/types';
import { generateDraft, getChatResponse } from '@/services/geminiService';
import { sendSubmission } from '@/services/formSubmitService';
import {
  ensureNotificationChannel,
  notifyAutomatedEmail,
} from '@/services/notificationService';
import { preferences } from '@/services/preferences';
import { formatDateTime } from '@/utils/format';

/**
 * Port of `app/src/main/java/com/example/viewmodel/AppViewModel.kt`.
 *
 * `StateFlow` → Zustand slices. Components subscribe with selectors so a change
 * to, say, `chatMessages` cannot re-render the meetings list (the Compose
 * `collectAsState()` per-flow granularity is preserved rather than degraded into
 * one giant context value).
 */

const INITIAL_CHAT_GREETING =
  "Hello! I am VaanAI, your virtual consultant. How can I assist you with VAAN Consulting's cloud, data, and agility platform architectures today?";

const initialChatMessages: ChatMessage[] = [{ role: 'model', text: INITIAL_CHAT_GREETING }];

export type AppState = {
  // --- Data flows backed reactively by the repository ------------------------
  meetings: ClientMeeting[];
  inquiries: ClientInquiry[];
  appointments: Appointment[];
  emailLogs: EmailLog[];

  // --- UI / settings flows ---------------------------------------------------
  autoEmailEnabled: boolean;
  aiDraftState: AiDraftState;
  notificationPermissionPrompt: boolean;
  bookmarkedArticles: ReadonlySet<string>;
  bookmarkedServices: ReadonlySet<string>;
  chatMessages: ChatMessage[];
  isChatLoading: boolean;
  isInitialised: boolean;

  // --- Lifecycle -------------------------------------------------------------
  initialise: (dao?: AppDao) => Promise<void>;

  // --- Settings --------------------------------------------------------------
  setAutoEmailEnabled: (enabled: boolean) => void;
  dismissNotificationPrompt: () => void;
  triggerPermissionPrompt: () => void;

  // --- Create / update operations -------------------------------------------
  scheduleMeeting: (args: {
    clientName: string;
    clientEmail: string;
    title: string;
    description: string;
    dateTime: number;
    meetingLink: string;
  }) => Promise<void>;
  cancelMeeting: (meeting: ClientMeeting) => Promise<void>;
  submitInquiry: (args: {
    clientName: string;
    clientEmail: string;
    companyName: string;
    subject: string;
    message: string;
  }) => Promise<void>;
  submitInquiryReply: (inquiry: ClientInquiry, replyText: string) => Promise<void>;
  createAppointment: (args: {
    clientName: string;
    clientEmail: string;
    serviceType: string;
    dateTime: number;
    durationMinutes: number;
    notes: string;
  }) => Promise<void>;
  confirmAppointment: (appointment: Appointment) => Promise<void>;
  completeAppointment: (appointment: Appointment) => Promise<void>;

  // --- Gemini AI drafting ----------------------------------------------------
  generateAIEmailDraftForInquiry: (inquiry: ClientInquiry) => Promise<void>;
  generateAIEmailDraftForAppointment: (appointment: Appointment) => Promise<void>;
  resetAiState: () => void;

  // --- Bookmarks -------------------------------------------------------------
  toggleArticleBookmark: (articleId: string) => Promise<void>;
  toggleServiceBookmark: (serviceId: string) => Promise<void>;

  // --- Chatbot ---------------------------------------------------------------
  sendChatMessage: (text: string) => Promise<void>;
  clearChatHistory: () => void;
};

/**
 * Repository handle. Created during `initialise()` so tests can inject an
 * in-memory DAO exactly as `AppDatabase.getDatabase` did under Robolectric.
 */
let repository: AppRepository | null = null;
let unsubscribeRepository: (() => void) | null = null;

/** Exposed for tests/teardown. */
export function __resetAppStoreRepository(): void {
  unsubscribeRepository?.();
  unsubscribeRepository = null;
  repository = null;
}

function requireRepository(): AppRepository {
  if (!repository) {
    // Defensive: keeps the UI functional (in-memory) rather than throwing if a
    // mutation somehow races ahead of initialisation.
    repository = new AppRepository(new InMemoryAppDao());
  }
  return repository;
}

export const useAppStore = create<AppState>()((set, get) => {
  /** Equivalent of `simulateAndLogAutomatedEmail(...)`. */
  const simulateAndLogAutomatedEmail = async (args: {
    recipient: string;
    subject: string;
    body: string;
    triggerEvent: string;
    onSuccess?: () => Promise<void> | void;
  }): Promise<void> => {
    await requireRepository().insertEmailLog({
      recipient: args.recipient,
      subject: args.subject,
      body: args.body,
      sentTime: Date.now(),
      triggerEvent: args.triggerEvent,
      status: 'Sent',
    });

    await args.onSuccess?.();

    const result = await notifyAutomatedEmail(args.recipient, args.subject);
    if (result === 'permission-denied') {
      set({ notificationPermissionPrompt: true });
    }
  };

  return {
    ...emptySnapshot,
    autoEmailEnabled: true,
    aiDraftState: { kind: 'idle' },
    notificationPermissionPrompt: false,
    bookmarkedArticles: new Set<string>(),
    bookmarkedServices: new Set<string>(),
    chatMessages: initialChatMessages,
    isChatLoading: false,
    isInitialised: false,

    // ------------------------------------------------------------- lifecycle

    initialise: async (dao?: AppDao) => {
      if (get().isInitialised && !dao) return;

      __resetAppStoreRepository();

      let resolvedDao: AppDao;
      if (dao) {
        resolvedDao = dao;
      } else {
        try {
          resolvedDao = await createSqliteDao();
        } catch (error) {
          const reason = error instanceof Error ? error.name : 'UnknownError';
          console.warn(`[AppStore] SQLite unavailable (${reason}); using in-memory store.`);
          resolvedDao = new InMemoryAppDao();
        }
      }

      repository = new AppRepository(resolvedDao);
      unsubscribeRepository = repository.subscribe((snapshot) => set(snapshot));

      // Matches AppViewModel.init { createNotificationChannel(); seed…; load bookmarks }
      void ensureNotificationChannel().catch(() => undefined);

      await seedInitialDataIfNecessary(repository);

      const [articles, services] = await Promise.all([
        preferences.getBookmarkedArticles(),
        preferences.getBookmarkedServices(),
      ]);

      set({
        ...repository.getSnapshot(),
        bookmarkedArticles: articles,
        bookmarkedServices: services,
        isInitialised: true,
      });
    },

    // -------------------------------------------------------------- settings

    setAutoEmailEnabled: (enabled) => set({ autoEmailEnabled: enabled }),
    dismissNotificationPrompt: () => set({ notificationPermissionPrompt: false }),
    triggerPermissionPrompt: () => set({ notificationPermissionPrompt: true }),

    // ------------------------------------------------------------- mutations

    scheduleMeeting: async ({
      clientName,
      clientEmail,
      title,
      description,
      dateTime,
      meetingLink,
    }) => {
      const repo = requireRepository();
      const meetingId = await repo.insertMeeting({
        clientName,
        clientEmail,
        title,
        description,
        dateTime,
        status: 'Scheduled',
        meetingLink,
        notificationSent: false,
      });

      if (!get().autoEmailEnabled) return;

      await simulateAndLogAutomatedEmail({
        recipient: clientEmail,
        subject: `Confirmed: Vaan Consulting Scheduled Meeting - ${title}`,
        body: `Dear ${clientName},

This is an automated confirmation of our scheduled meeting.

Meeting: ${title}
Description: ${description}
Time: ${formatDateTime(dateTime)}
Google Meet Video Link: ${meetingLink}

We look forward to speaking with you. If you need to make changes, please reschedule via our client portal or reply to this message.

Best regards,
Vaan Consulting Support
https://www.vaanconsulting.com/`,
        triggerEvent: 'Meeting Scheduled',
        onSuccess: async () => {
          await repo.updateMeeting({
            id: meetingId,
            clientName,
            clientEmail,
            title,
            description,
            dateTime,
            status: 'Scheduled',
            meetingLink,
            notificationSent: true,
          });
        },
      });
    },

    cancelMeeting: async (meeting) => {
      await requireRepository().updateMeeting({ ...meeting, status: 'Cancelled' });

      if (!get().autoEmailEnabled) return;

      await simulateAndLogAutomatedEmail({
        recipient: meeting.clientEmail,
        subject: `Cancelled: Vaan Consulting Scheduled Meeting - ${meeting.title}`,
        body: `Dear ${meeting.clientName},

This notification is to confirm that our scheduled meeting "${meeting.title}" has been cancelled.

If this was in error, or you would like to reschedule, please feel free to create a new appointment slot in your Vaan Client App.

Warm regards,
Vaan Consulting Operations
https://www.vaanconsulting.com/`,
        triggerEvent: 'Meeting Cancelled',
      });
    },

    submitInquiry: async ({ clientName, clientEmail, companyName, subject, message }) => {
      await requireRepository().insertInquiry({
        clientName,
        clientEmail,
        companyName,
        subject,
        message,
        receivedTime: Date.now(),
        status: 'New',
        replyMessage: null,
      });

      if (get().autoEmailEnabled) {
        await simulateAndLogAutomatedEmail({
          recipient: clientEmail,
          subject: `Received: Vaan Consulting - ${subject}`,
          body: `Dear ${clientName},

We have received your technical inquiry regarding "${subject}" for ${companyName}.

One of our lead IT consultants will review your brief and follow up within one business hour.

Inquiry Message Summary:
"${message}"

Sincerely,
Customer Relations Team
Vaan Consulting
https://www.vaanconsulting.com/`,
          triggerEvent: 'Inquiry Auto-Response',
        });
      }

      // Fire-and-forget real dispatch, mirroring the detached `launch { … }`.
      void sendSubmission({
        name: clientName,
        email: clientEmail,
        company: companyName,
        service: subject,
        message,
        subjectLine: `VAAN Consulting Mobile - New Inquiry: ${subject} from ${clientName}`,
      });
    },

    submitInquiryReply: async (inquiry, replyText) => {
      await requireRepository().updateInquiry({
        ...inquiry,
        status: 'Replied',
        replyMessage: replyText,
      });

      await simulateAndLogAutomatedEmail({
        recipient: inquiry.clientEmail,
        subject: `Re: ${inquiry.subject} - Vaan Consulting Reply`,
        body: replyText,
        triggerEvent: 'Inquiry Reply',
      });
    },

    createAppointment: async ({
      clientName,
      clientEmail,
      serviceType,
      dateTime,
      durationMinutes,
      notes,
    }) => {
      await requireRepository().insertAppointment({
        clientName,
        clientEmail,
        serviceType,
        dateTime,
        durationMinutes,
        notes,
        status: 'Pending',
        isEmailSent: false,
      });

      if (get().autoEmailEnabled) {
        await simulateAndLogAutomatedEmail({
          recipient: clientEmail,
          subject: `Booking Received: Vaan Consulting - ${serviceType}`,
          body: `Dear ${clientName},

Thank you for booking a technical consulting appointment with us.

Service Type: ${serviceType}
Proposed Time: ${formatDateTime(dateTime)} (${durationMinutes} Minutes)
Proposed Notes: ${notes}

Your appointment request is currently "Pending" confirmation. A Vaan account manager will review the requirements and confirm the slot shortly. An automated confirmation email will follow.

Warm regards,
The Team at Vaan Consulting
https://www.vaanconsulting.com/`,
          triggerEvent: 'Appointment Booking Request Received',
        });
      }

      void sendSubmission({
        name: clientName,
        email: clientEmail,
        company: 'Discovery Call Booking',
        service: serviceType,
        message: `Booking Request Details:
- Service Category: ${serviceType}
- Proposed Date/Time: ${formatDateTime(dateTime)}
- Proposed Duration: ${durationMinutes} Minutes
- Additional Client Notes: ${notes}`,
        subjectLine: `VAAN Consulting Mobile - Discovery Call Booking: ${serviceType} from ${clientName}`,
      });
    },

    confirmAppointment: async (appointment) => {
      await requireRepository().updateAppointment({
        ...appointment,
        status: 'Confirmed',
        isEmailSent: true,
      });

      if (!get().autoEmailEnabled) return;

      await simulateAndLogAutomatedEmail({
        recipient: appointment.clientEmail,
        subject: `CONFIRMED: Vaan Technical Discovery - ${appointment.serviceType}`,
        body: `Dear ${appointment.clientName},

We are pleased to confirm your technical discovery session with Vaan Consulting.

Session Category: ${appointment.serviceType}
Confirmed Time: ${formatDateTime(appointment.dateTime)} (${appointment.durationMinutes} Minutes)
Meeting Access: Google Meet link is attached to your calendar.
Briefing Notes: ${appointment.notes}

Our consulting lead will join the conference call prepared to discuss your architectural roadmap. Please ensure any technical designs or repository accesses are shared in advance.

See you there!

Operations Desk
Vaan Consulting
https://www.vaanconsulting.com/`,
        triggerEvent: 'Appointment Confirmed',
      });
    },

    completeAppointment: async (appointment) => {
      await requireRepository().updateAppointment({ ...appointment, status: 'Completed' });
    },

    // ------------------------------------------------------- AI draft flow

    generateAIEmailDraftForInquiry: async (inquiry) => {
      set({ aiDraftState: { kind: 'loading' } });

      const prompt = `You are a senior IT consulting specialist at Vaan Consulting (https://www.vaanconsulting.com/).
Draft a highly professional, welcoming, and intelligent response to a client's technical inquiry.

Client Name: ${inquiry.clientName}
Company: ${inquiry.companyName}
Inquiry Subject: ${inquiry.subject}
Inquiry Message: ${inquiry.message}

Write a response addressing their subject, providing modern high-level consultative suggestions (e.g., AWS Cloud architecture, low-latency Snowflake pipelines, secure offline-first Android apps, enterprise React web solutions), and proposing a 15-minute alignment call. Ensure it is written in a professional, courteous corporate consulting tone. Do not use generic placeholders. Signature should be "Vaan Consulting Expert Team".`;

      const draft = await generateDraft(
        prompt,
        'You are a professional IT consultant at Vaan Consulting drafting technical client replies.',
      );

      set({ aiDraftState: { kind: 'success', draft } });
    },

    generateAIEmailDraftForAppointment: async (appointment) => {
      set({ aiDraftState: { kind: 'loading' } });

      const prompt = `You are a principal systems engineer at Vaan Consulting.
Draft a technical consultation briefing email for a newly confirmed appointment.

Client Name: ${appointment.clientName}
Service Topic: ${appointment.serviceType}
Appointment Notes: ${appointment.notes}
Appointment Date: ${formatDateTime(appointment.dateTime)}

Write a brief, highly technical preparation email outlining what files, accesses, or goals the client should think about prior to this appointment. Use bullet points for structural clarity. Signature should be "Technical Advisory, Vaan Consulting".`;

      const draft = await generateDraft(
        prompt,
        'You are a lead technical architect at Vaan Consulting drafting a preparation brief.',
      );

      set({ aiDraftState: { kind: 'success', draft } });
    },

    resetAiState: () => set({ aiDraftState: { kind: 'idle' } }),

    // -------------------------------------------------------------- bookmarks

    toggleArticleBookmark: async (articleId) => {
      const current = new Set(get().bookmarkedArticles);
      if (current.has(articleId)) current.delete(articleId);
      else current.add(articleId);
      set({ bookmarkedArticles: current });
      await preferences.setBookmarkedArticles(current);
    },

    toggleServiceBookmark: async (serviceId) => {
      const current = new Set(get().bookmarkedServices);
      if (current.has(serviceId)) current.delete(serviceId);
      else current.add(serviceId);
      set({ bookmarkedServices: current });
      await preferences.setBookmarkedServices(current);
    },

    // ---------------------------------------------------------------- chatbot

    sendChatMessage: async (text) => {
      const trimmed = text.trim();
      if (trimmed.length === 0) return;

      const withUserTurn: ChatMessage[] = [...get().chatMessages, { role: 'user', text: trimmed }];
      set({ chatMessages: withUserTurn, isChatLoading: true });

      // History excludes the message we are about to send, matching `dropLast(1)`.
      const history = withUserTurn.slice(0, -1).map((m) => ({ sender: m.role, text: m.text }));
      const response = await getChatResponse(trimmed, history);

      set((state) => ({
        isChatLoading: false,
        chatMessages: [...state.chatMessages, { role: 'model', text: response }],
      }));
    },

    clearChatHistory: () => set({ chatMessages: initialChatMessages }),
  };
});
