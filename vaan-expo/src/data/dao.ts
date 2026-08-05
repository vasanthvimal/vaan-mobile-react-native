import type {
  Appointment,
  ClientInquiry,
  ClientMeeting,
  EmailLog,
  NewAppointment,
  NewClientInquiry,
  NewClientMeeting,
  NewEmailLog,
} from './types';

/**
 * Port of `AppDao.kt`.
 *
 * Room's `Flow<List<T>>` return types become explicit `getAll*` reads plus a
 * change notification (see `AppRepository`), which is the idiomatic React
 * equivalent of a cold flow feeding a `StateFlow`.
 */
export type AppDao = {
  // Client Meetings — "SELECT * FROM meetings ORDER BY dateTime ASC"
  getAllMeetings(): Promise<ClientMeeting[]>;
  insertMeeting(meeting: NewClientMeeting): Promise<number>;
  updateMeeting(meeting: ClientMeeting): Promise<void>;
  deleteMeeting(meeting: ClientMeeting): Promise<void>;
  deleteMeetingById(id: number): Promise<void>;

  // Client Inquiries — "SELECT * FROM inquiries ORDER BY receivedTime DESC"
  getAllInquiries(): Promise<ClientInquiry[]>;
  insertInquiry(inquiry: NewClientInquiry): Promise<number>;
  updateInquiry(inquiry: ClientInquiry): Promise<void>;
  deleteInquiry(inquiry: ClientInquiry): Promise<void>;

  // Appointments — "SELECT * FROM appointments ORDER BY dateTime ASC"
  getAllAppointments(): Promise<Appointment[]>;
  insertAppointment(appointment: NewAppointment): Promise<number>;
  updateAppointment(appointment: Appointment): Promise<void>;
  deleteAppointment(appointment: Appointment): Promise<void>;

  // Email Logs — "SELECT * FROM email_logs ORDER BY sentTime DESC"
  getAllEmailLogs(): Promise<EmailLog[]>;
  insertEmailLog(log: NewEmailLog): Promise<number>;
};

/** Matches `@Database(version = 1)` in AppDatabase.kt. */
export const DATABASE_NAME = 'vaan_consulting_db';
export const DATABASE_VERSION = 1;

/**
 * Room generated these tables from the `@Entity` annotations. Column names and
 * affinities are reproduced 1:1 so an existing Room database file remains
 * readable if it is ever migrated across.
 */
export const SCHEMA_SQL = `
PRAGMA journal_mode = WAL;
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS meetings (
  id               INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  clientName       TEXT    NOT NULL,
  clientEmail      TEXT    NOT NULL,
  title            TEXT    NOT NULL,
  description      TEXT    NOT NULL,
  dateTime         INTEGER NOT NULL,
  status           TEXT    NOT NULL,
  meetingLink      TEXT    NOT NULL,
  notificationSent INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS inquiries (
  id           INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  clientName   TEXT    NOT NULL,
  clientEmail  TEXT    NOT NULL,
  companyName  TEXT    NOT NULL,
  subject      TEXT    NOT NULL,
  message      TEXT    NOT NULL,
  receivedTime INTEGER NOT NULL,
  status       TEXT    NOT NULL,
  replyMessage TEXT
);

CREATE TABLE IF NOT EXISTS appointments (
  id              INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  clientName      TEXT    NOT NULL,
  clientEmail     TEXT    NOT NULL,
  serviceType     TEXT    NOT NULL,
  dateTime        INTEGER NOT NULL,
  durationMinutes INTEGER NOT NULL DEFAULT 30,
  notes           TEXT    NOT NULL,
  status          TEXT    NOT NULL,
  isEmailSent     INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS email_logs (
  id           INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  recipient    TEXT    NOT NULL,
  subject      TEXT    NOT NULL,
  body         TEXT    NOT NULL,
  sentTime     INTEGER NOT NULL,
  triggerEvent TEXT    NOT NULL,
  status       TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_meetings_dateTime      ON meetings (dateTime ASC);
CREATE INDEX IF NOT EXISTS idx_inquiries_receivedTime ON inquiries (receivedTime DESC);
CREATE INDEX IF NOT EXISTS idx_appointments_dateTime  ON appointments (dateTime ASC);
CREATE INDEX IF NOT EXISTS idx_email_logs_sentTime    ON email_logs (sentTime DESC);
`;
