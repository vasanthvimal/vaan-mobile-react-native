import * as SQLite from 'expo-sqlite';

import { DATABASE_NAME, SCHEMA_SQL, type AppDao } from './dao';
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
 * SQLite-backed implementation of {@link AppDao}, replacing Room.
 *
 * Security: every statement is parameterised (`?` placeholders). No user-supplied
 * value is ever concatenated into SQL, which removes the SQL-injection surface
 * (OWASP A03) that hand-rolled query building would introduce.
 */

type MeetingRow = Omit<ClientMeeting, 'notificationSent'> & { notificationSent: number };
type AppointmentRow = Omit<Appointment, 'isEmailSent'> & { isEmailSent: number };

const toMeeting = (row: MeetingRow): ClientMeeting => ({
  ...row,
  notificationSent: row.notificationSent === 1,
});

const toAppointment = (row: AppointmentRow): Appointment => ({
  ...row,
  isEmailSent: row.isEmailSent === 1,
});

export class SqliteAppDao implements AppDao {
  constructor(private readonly db: SQLite.SQLiteDatabase) {}

  // ---------------------------------------------------------------- meetings

  async getAllMeetings(): Promise<ClientMeeting[]> {
    const rows = await this.db.getAllAsync<MeetingRow>(
      'SELECT * FROM meetings ORDER BY dateTime ASC',
    );
    return rows.map(toMeeting);
  }

  async insertMeeting(meeting: NewClientMeeting): Promise<number> {
    const result = await this.db.runAsync(
      `INSERT OR REPLACE INTO meetings
         (id, clientName, clientEmail, title, description, dateTime, status, meetingLink, notificationSent)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        meeting.id ?? null,
        meeting.clientName,
        meeting.clientEmail,
        meeting.title,
        meeting.description,
        meeting.dateTime,
        meeting.status,
        meeting.meetingLink,
        meeting.notificationSent ? 1 : 0,
      ],
    );
    return result.lastInsertRowId;
  }

  async updateMeeting(meeting: ClientMeeting): Promise<void> {
    await this.db.runAsync(
      `UPDATE meetings SET
         clientName = ?, clientEmail = ?, title = ?, description = ?,
         dateTime = ?, status = ?, meetingLink = ?, notificationSent = ?
       WHERE id = ?`,
      [
        meeting.clientName,
        meeting.clientEmail,
        meeting.title,
        meeting.description,
        meeting.dateTime,
        meeting.status,
        meeting.meetingLink,
        meeting.notificationSent ? 1 : 0,
        meeting.id,
      ],
    );
  }

  async deleteMeeting(meeting: ClientMeeting): Promise<void> {
    await this.deleteMeetingById(meeting.id);
  }

  async deleteMeetingById(id: number): Promise<void> {
    await this.db.runAsync('DELETE FROM meetings WHERE id = ?', [id]);
  }

  // --------------------------------------------------------------- inquiries

  async getAllInquiries(): Promise<ClientInquiry[]> {
    return this.db.getAllAsync<ClientInquiry>(
      'SELECT * FROM inquiries ORDER BY receivedTime DESC',
    );
  }

  async insertInquiry(inquiry: NewClientInquiry): Promise<number> {
    const result = await this.db.runAsync(
      `INSERT OR REPLACE INTO inquiries
         (id, clientName, clientEmail, companyName, subject, message, receivedTime, status, replyMessage)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        inquiry.id ?? null,
        inquiry.clientName,
        inquiry.clientEmail,
        inquiry.companyName,
        inquiry.subject,
        inquiry.message,
        inquiry.receivedTime,
        inquiry.status,
        inquiry.replyMessage ?? null,
      ],
    );
    return result.lastInsertRowId;
  }

  async updateInquiry(inquiry: ClientInquiry): Promise<void> {
    await this.db.runAsync(
      `UPDATE inquiries SET
         clientName = ?, clientEmail = ?, companyName = ?, subject = ?,
         message = ?, receivedTime = ?, status = ?, replyMessage = ?
       WHERE id = ?`,
      [
        inquiry.clientName,
        inquiry.clientEmail,
        inquiry.companyName,
        inquiry.subject,
        inquiry.message,
        inquiry.receivedTime,
        inquiry.status,
        inquiry.replyMessage ?? null,
        inquiry.id,
      ],
    );
  }

  async deleteInquiry(inquiry: ClientInquiry): Promise<void> {
    await this.db.runAsync('DELETE FROM inquiries WHERE id = ?', [inquiry.id]);
  }

  // ------------------------------------------------------------ appointments

  async getAllAppointments(): Promise<Appointment[]> {
    const rows = await this.db.getAllAsync<AppointmentRow>(
      'SELECT * FROM appointments ORDER BY dateTime ASC',
    );
    return rows.map(toAppointment);
  }

  async insertAppointment(appointment: NewAppointment): Promise<number> {
    const result = await this.db.runAsync(
      `INSERT OR REPLACE INTO appointments
         (id, clientName, clientEmail, serviceType, dateTime, durationMinutes, notes, status, isEmailSent)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        appointment.id ?? null,
        appointment.clientName,
        appointment.clientEmail,
        appointment.serviceType,
        appointment.dateTime,
        appointment.durationMinutes,
        appointment.notes,
        appointment.status,
        appointment.isEmailSent ? 1 : 0,
      ],
    );
    return result.lastInsertRowId;
  }

  async updateAppointment(appointment: Appointment): Promise<void> {
    await this.db.runAsync(
      `UPDATE appointments SET
         clientName = ?, clientEmail = ?, serviceType = ?, dateTime = ?,
         durationMinutes = ?, notes = ?, status = ?, isEmailSent = ?
       WHERE id = ?`,
      [
        appointment.clientName,
        appointment.clientEmail,
        appointment.serviceType,
        appointment.dateTime,
        appointment.durationMinutes,
        appointment.notes,
        appointment.status,
        appointment.isEmailSent ? 1 : 0,
        appointment.id,
      ],
    );
  }

  async deleteAppointment(appointment: Appointment): Promise<void> {
    await this.db.runAsync('DELETE FROM appointments WHERE id = ?', [appointment.id]);
  }

  // -------------------------------------------------------------- email logs

  async getAllEmailLogs(): Promise<EmailLog[]> {
    return this.db.getAllAsync<EmailLog>('SELECT * FROM email_logs ORDER BY sentTime DESC');
  }

  async insertEmailLog(log: NewEmailLog): Promise<number> {
    const result = await this.db.runAsync(
      `INSERT OR REPLACE INTO email_logs
         (id, recipient, subject, body, sentTime, triggerEvent, status)
       VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [
        log.id ?? null,
        log.recipient,
        log.subject,
        log.body,
        log.sentTime,
        log.triggerEvent,
        log.status,
      ],
    );
    return result.lastInsertRowId;
  }
}

let dbPromise: Promise<SQLite.SQLiteDatabase> | null = null;

/**
 * Equivalent of `AppDatabase.getDatabase(context)` — a process-wide singleton
 * created lazily, guarded against the concurrent-open race the Kotlin
 * `synchronized` block protected against.
 */
export function openAppDatabase(): Promise<SQLite.SQLiteDatabase> {
  if (!dbPromise) {
    dbPromise = (async () => {
      const db = await SQLite.openDatabaseAsync(DATABASE_NAME);
      await db.execAsync(SCHEMA_SQL);
      return db;
    })().catch((error: unknown) => {
      // Allow a later retry rather than caching a rejected promise forever.
      dbPromise = null;
      throw error;
    });
  }
  return dbPromise;
}

export async function createSqliteDao(): Promise<SqliteAppDao> {
  return new SqliteAppDao(await openAppDatabase());
}

/** Test/teardown helper — drops the cached singleton. */
export function resetAppDatabase(): void {
  dbPromise = null;
}
