/**
 * Date/time formatting helpers reproducing the exact `java.text.SimpleDateFormat`
 * patterns used across the Kotlin app.
 *
 * Implemented on top of `Intl.DateTimeFormat` (available in Hermes with the
 * full-ICU build Expo ships) and composed manually rather than relying on a
 * single `Intl` skeleton, because `SimpleDateFormat` produces zero-padded
 * 12-hour values and a fixed separator layout that `Intl` presets do not match.
 * This keeps output byte-identical, e.g. "Aug 05, 2026 - 03:45 PM".
 */

const pad2 = (n: number): string => (n < 10 ? `0${n}` : `${n}`);

type LocaleArg = string | undefined;

const monthShortCache = new Map<string, Intl.DateTimeFormat>();
const monthLongCache = new Map<string, Intl.DateTimeFormat>();
const weekdayLongCache = new Map<string, Intl.DateTimeFormat>();

function cachedFormatter(
  cache: Map<string, Intl.DateTimeFormat>,
  locale: LocaleArg,
  options: Intl.DateTimeFormatOptions,
): Intl.DateTimeFormat {
  const key = `${locale ?? 'default'}|${JSON.stringify(options)}`;
  let formatter = cache.get(key);
  if (!formatter) {
    formatter = new Intl.DateTimeFormat(locale, options);
    cache.set(key, formatter);
  }
  return formatter;
}

/** `MMM` — abbreviated month name. */
export const monthShort = (date: Date, locale?: LocaleArg): string =>
  cachedFormatter(monthShortCache, locale, { month: 'short' }).format(date);

/** `MMMM` — full month name. */
export const monthLong = (date: Date, locale?: LocaleArg): string =>
  cachedFormatter(monthLongCache, locale, { month: 'long' }).format(date);

/** `EEEE` — full weekday name. */
export const weekdayLong = (date: Date, locale?: LocaleArg): string =>
  cachedFormatter(weekdayLongCache, locale, { weekday: 'long' }).format(date);

/** `hh:mm a` — zero-padded 12-hour clock with an uppercase meridiem. */
export function clock12(date: Date): string {
  const hours24 = date.getHours();
  const meridiem = hours24 < 12 ? 'AM' : 'PM';
  const hours12 = hours24 % 12 === 0 ? 12 : hours24 % 12;
  return `${pad2(hours12)}:${pad2(date.getMinutes())} ${meridiem}`;
}

/** `SimpleDateFormat("MMM dd, yyyy - hh:mm a")` — AppViewModel.formatDateTime */
export function formatDateTime(timestamp: number): string {
  const date = new Date(timestamp);
  return `${monthShort(date)} ${pad2(date.getDate())}, ${date.getFullYear()} - ${clock12(date)}`;
}

/** `SimpleDateFormat("MMMM dd, yyyy", Locale.US)` — booking form date picker label */
export function formatLongDate(timestamp: number): string {
  const date = new Date(timestamp);
  return `${monthLong(date, 'en-US')} ${pad2(date.getDate())}, ${date.getFullYear()}`;
}

/** `SimpleDateFormat("hh:mm a", Locale.US)` — booking form time picker label */
export function formatTimeOnly(timestamp: number): string {
  return clock12(new Date(timestamp));
}

/** `SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US)` — booking review summary */
export function formatLongDateTime(timestamp: number): string {
  const date = new Date(timestamp);
  return `${monthLong(date, 'en-US')} ${pad2(date.getDate())}, ${date.getFullYear()} ${clock12(date)}`;
}

/** `SimpleDateFormat("EEEE, MMM dd, yyyy 'at' hh:mm a")` — meeting cards */
export function formatMeetingDateTime(timestamp: number): string {
  const date = new Date(timestamp);
  return `${weekdayLong(date)}, ${monthShort(date)} ${pad2(date.getDate())}, ${date.getFullYear()} at ${clock12(date)}`;
}

/** `SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a")` — inquiry & appointment cards */
export function formatCardDateTime(timestamp: number): string {
  const date = new Date(timestamp);
  return `${monthShort(date)} ${pad2(date.getDate())}, ${date.getFullYear()} at ${clock12(date)}`;
}

/** `SimpleDateFormat("MMM dd, hh:mm a")` — outbox log rows */
export function formatOutboxDateTime(timestamp: number): string {
  const date = new Date(timestamp);
  return `${monthShort(date)} ${pad2(date.getDate())}, ${clock12(date)}`;
}
