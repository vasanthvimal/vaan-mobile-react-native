import {
  formatCardDateTime,
  formatDateTime,
  formatLongDate,
  formatLongDateTime,
  formatMeetingDateTime,
  formatOutboxDateTime,
  formatTimeOnly,
} from './format';

describe('format utilities', () => {
  const date = new Date(2026, 7, 5, 15, 4, 0, 0); // Aug 05, 2026 03:04 PM local
  const ts = date.getTime();

  it('matches AppViewModel.formatDateTime pattern', () => {
    expect(formatDateTime(ts)).toMatch(/^[A-Za-z]{3} \d{2}, \d{4} - \d{2}:\d{2} (AM|PM)$/);
  });

  it('renders long date in Locale.US style', () => {
    expect(formatLongDate(ts)).toMatch(/^[A-Za-z]+ \d{2}, \d{4}$/);
  });

  it('renders time-only and long datetime forms', () => {
    expect(formatTimeOnly(ts)).toMatch(/^\d{2}:\d{2} (AM|PM)$/);
    expect(formatLongDateTime(ts)).toMatch(/^[A-Za-z]+ \d{2}, \d{4} \d{2}:\d{2} (AM|PM)$/);
  });

  it('renders card-specific date formats', () => {
    expect(formatMeetingDateTime(ts)).toContain(' at ');
    expect(formatCardDateTime(ts)).toContain(' at ');
    expect(formatOutboxDateTime(ts)).toMatch(/^[A-Za-z]{3} \d{2}, \d{2}:\d{2} (AM|PM)$/);
  });
});
