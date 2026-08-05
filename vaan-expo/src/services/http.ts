/**
 * Small `fetch` wrapper replacing OkHttp.
 *
 * Reproduces the OkHttp client configuration used by `GeminiHelper` /
 * `FormSubmitHelper` (explicit connect/read/write timeouts) using
 * `AbortController`, which React Native's `fetch` honours on both platforms.
 *
 * Security notes:
 *  - Plain-HTTP URLs are rejected outright, matching the `NSAllowsArbitraryLoads:
 *    false` / Android `cleartextTrafficPermitted=false` posture.
 *  - Response bodies are capped so a hostile or malfunctioning endpoint cannot
 *    exhaust device memory.
 *  - Errors never include the request body or headers, so keys cannot leak into
 *    logs or crash reports.
 */

export class HttpError extends Error {
  constructor(
    readonly status: number,
    message: string,
  ) {
    super(message);
    this.name = 'HttpError';
  }
}

export class HttpTimeoutError extends Error {
  constructor(readonly timeoutMs: number) {
    super(`Request timed out after ${timeoutMs}ms`);
    this.name = 'HttpTimeoutError';
  }
}

/** 4 MB — far above any legitimate response from the endpoints we call. */
const MAX_RESPONSE_BYTES = 4 * 1024 * 1024;

export type PostJsonOptions = {
  url: string;
  body: unknown;
  timeoutMs: number;
  headers?: Record<string, string>;
  signal?: AbortSignal;
};

export async function postJson<T = unknown>({
  url,
  body,
  timeoutMs,
  headers,
  signal,
}: PostJsonOptions): Promise<T> {
  if (!/^https:\/\//i.test(url)) {
    throw new HttpError(0, 'Refusing to send request over a non-HTTPS connection');
  }

  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeoutMs);

  const onExternalAbort = () => controller.abort();
  signal?.addEventListener('abort', onExternalAbort);

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json; charset=utf-8',
        Accept: 'application/json',
        ...headers,
      },
      body: JSON.stringify(body),
      signal: controller.signal,
    });

    const text = await response.text();

    if (text.length > MAX_RESPONSE_BYTES) {
      throw new HttpError(response.status, 'Response exceeded the maximum allowed size');
    }

    if (!response.ok) {
      throw new HttpError(response.status, `Request failed with status ${response.status}`);
    }

    if (text.length === 0) {
      return undefined as T;
    }

    return JSON.parse(text) as T;
  } catch (error) {
    if (error instanceof Error && error.name === 'AbortError') {
      throw new HttpTimeoutError(timeoutMs);
    }
    throw error;
  } finally {
    clearTimeout(timer);
    signal?.removeEventListener('abort', onExternalAbort);
  }
}
