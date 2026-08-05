import Storage from 'expo-sqlite/kv-store';

/**
 * Replacement for the Android `SharedPreferences("vaan_prefs", MODE_PRIVATE)`
 * used by `AppViewModel` to persist bookmarks.
 *
 * `expo-sqlite/kv-store` is used rather than AsyncStorage because it ships with
 * the SQLite dependency we already need (no extra native module), offers a
 * synchronous API for reads on the JS thread, and is backed by the app's private
 * sandbox on both platforms.
 *
 * Security: bookmarks are non-sensitive identifiers, so plain (non-encrypted)
 * key/value storage is appropriate. Nothing sensitive — no tokens, no PII — is
 * written here; see docs/SECURITY_ASSESSMENT.md.
 */

const KEY_BOOKMARKED_ARTICLES = 'bookmarked_articles';
const KEY_BOOKMARKED_SERVICES = 'bookmarked_services';

/** Android `getStringSet` semantics: unordered, unique. Persisted as a JSON array. */
async function readStringSet(key: string): Promise<Set<string>> {
  try {
    const raw = await Storage.getItem(key);
    if (!raw) return new Set();
    const parsed: unknown = JSON.parse(raw);
    if (!Array.isArray(parsed)) return new Set();
    return new Set(parsed.filter((value): value is string => typeof value === 'string'));
  } catch {
    // Corrupt/unreadable preferences must never block app start.
    return new Set();
  }
}

async function writeStringSet(key: string, value: Set<string>): Promise<void> {
  try {
    await Storage.setItem(key, JSON.stringify([...value]));
  } catch (error) {
    const reason = error instanceof Error ? error.name : 'UnknownError';
    console.warn(`[Preferences] Failed to persist "${key}" (${reason}).`);
  }
}

export const preferences = {
  getBookmarkedArticles: () => readStringSet(KEY_BOOKMARKED_ARTICLES),
  setBookmarkedArticles: (value: Set<string>) => writeStringSet(KEY_BOOKMARKED_ARTICLES, value),
  getBookmarkedServices: () => readStringSet(KEY_BOOKMARKED_SERVICES),
  setBookmarkedServices: (value: Set<string>) => writeStringSet(KEY_BOOKMARKED_SERVICES, value),
};
