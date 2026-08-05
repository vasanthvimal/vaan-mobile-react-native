import { formspreeEndpoint, isSecureHttpsUrl } from './config';
import { postJson } from './http';

/**
 * Port of `app/src/main/java/com/example/data/FormSubmitHelper.kt`.
 *
 * Same JSON contract (`name`, `email`, `company?`, `service`, `message`,
 * `_subject`), same 20s timeout, same "return a boolean, never throw" behaviour
 * so callers keep their fire-and-forget semantics.
 *
 * Security: the endpoint is configurable and validated as HTTPS; submission
 * content is never written to logs (it contains client PII).
 */

const REQUEST_TIMEOUT_MS = 20_000;

export type FormSubmission = {
  name: string;
  email: string;
  company?: string | null;
  service: string;
  message: string;
  subjectLine: string;
};

export async function sendSubmission(
  submission: FormSubmission,
  signal?: AbortSignal,
): Promise<boolean> {
  if (!isSecureHttpsUrl(formspreeEndpoint)) {
    console.warn('[FormSubmitService] Endpoint is not a valid HTTPS URL; submission skipped.');
    return false;
  }

  const body: Record<string, string> = {
    name: submission.name,
    email: submission.email,
    service: submission.service,
    message: submission.message,
    _subject: submission.subjectLine,
  };

  if (submission.company != null) {
    body.company = submission.company;
  }

  try {
    await postJson({
      url: formspreeEndpoint,
      body,
      timeoutMs: REQUEST_TIMEOUT_MS,
      signal,
    });
    return true;
  } catch (error) {
    const reason = error instanceof Error ? error.name : 'UnknownError';
    console.warn(`[FormSubmitService] Submission failed (${reason}).`);
    return false;
  }
}
