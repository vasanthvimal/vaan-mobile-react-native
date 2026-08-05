import Constants from 'expo-constants';

/**
 * Central, typed access to build-time configuration.
 *
 * Migration note: the Kotlin app read `BuildConfig.GEMINI_API_KEY`, injected by
 * the Secrets Gradle Plugin. The Expo equivalent is `app.config.ts` → `extra`,
 * read here. Values are resolved once and never logged.
 */

type Extra = {
  geminiApiKey?: string;
  aiProxyUrl?: string;
  formspreeEndpoint?: string;
};

const extra: Extra = (Constants.expoConfig?.extra ?? {}) as Extra;

/** Placeholder shipped in `.env.example`; treated as "not configured". */
const PLACEHOLDER_KEY = 'MY_GEMINI_API_KEY';

const trim = (value: string | undefined): string => (value ?? '').trim();

export const geminiApiKey: string = (() => {
  const key = trim(extra.geminiApiKey);
  return key === PLACEHOLDER_KEY ? '' : key;
})();

/**
 * When set, all model traffic is routed through a first-party backend which
 * holds the API key. This is the recommended production configuration because
 * nothing secret is embedded in the distributed app binary.
 */
export const aiProxyUrl: string = trim(extra.aiProxyUrl);

export const formspreeEndpoint: string =
  trim(extra.formspreeEndpoint) || 'https://formspree.io/f/mvzjrrjj';

export const hasAiCredentials: boolean = aiProxyUrl.length > 0 || geminiApiKey.length > 0;

/**
 * Guards against a misconfigured `extra` pointing the app at a plaintext or
 * attacker-controlled endpoint. Only absolute HTTPS URLs are accepted.
 */
export function isSecureHttpsUrl(value: string): boolean {
  try {
    return new URL(value).protocol === 'https:';
  } catch {
    return false;
  }
}
