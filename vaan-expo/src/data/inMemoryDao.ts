import type { AppDao } from './dao';
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
 * In-memory {@link AppDao}, mirroring `Room.inMemoryDatabaseBuilder(...)` which
 * `AppDatabase.getDatabase` used when running under JUnit/Robolectric.
 *
 * Used by the Jest suite and as a safe fallback if the native SQLite module is
 * unavailable, so the UI never renders an error state it did not have before.
 */
export class InMemoryAppDao implements AppDao {
  private meetings: ClientMeeting[] = [];
  private inquiries: ClientInquiry[] = [];
  private appointments: Appointment[] = [];
  private emailLogs: EmailLog[] = [];
  private nextId = { meetings: 1, inquiries: 1, appointments: 1, emailLogs: 1 };

  // ---------------------------------------------------------------- meetings

  async getAllMeetings(): Promise<ClientMeeting[]> {
    return [...this.meetings].sort((a, b) => a.dateTime - b.dateTime);
  }

  async insertMeeting(meeting: NewClientMeeting): Promise<number> {
    const id = meeting.id && meeting.id > 0 ? meeting.id : this.nextId.meetings++;
    const record: ClientMeeting = { ...meeting, id };
    const existing = this.meetings.findIndex((m) => m.id === id);
    if (existing >= 0) this.meetings[existing] = record;
    else this.meetings.push(record);
    return id;
  }

  async updateMeeting(meeting: ClientMeeting): Promise<void> {
    const index = this.meetings.findIndex((m) => m.id === meeting.id);
    if (index >= 0) this.meetings[index] = { ...meeting };
  }

  async deleteMeeting(meeting: ClientMeeting): Promise<void> {
    await this.deleteMeetingById(meeting.id);
  }

  async deleteMeetingById(id: number): Promise<void> {
    this.meetings = this.meetings.filter((m) => m.id !== id);
  }

  // --------------------------------------------------------------- inquiries

  async getAllInquiries(): Promise<ClientInquiry[]> {
    return [...this.inquiries].sort((a, b) => b.receivedTime - a.receivedTime);
  }

  async insertInquiry(inquiry: NewClientInquiry): Promise<number> {
    const id = inquiry.id && inquiry.id > 0 ? inquiry.id : this.nextId.inquiries++;
    const record: ClientInquiry = { replyMessage: null, ...inquiry, id };
    const existing = this.inquiries.findIndex((i) => i.id === id);
    if (existing >= 0) this.inquiries[existing] = record;
    else this.inquiries.push(record);
    return id;
  }

  async updateInquiry(inquiry: ClientInquiry): Promise<void> {
    const index = this.inquiries.findIndex((i) => i.id === inquiry.id);
    if (index >= 0) this.inquiries[index] = { ...inquiry };
  }

  async deleteInquiry(inquiry: ClientInquiry): Promise<void> {
    this.inquiries = this.inquiries.filter((i) => i.id !== inquiry.id);
  }

  // ------------------------------------------------------------ appointments

  async getAllAppointments(): Promise<Appointment[]> {
    return [...this.appointments].sort((a, b) => a.dateTime - b.dateTime);
  }

  async insertAppointment(appointment: NewAppointment): Promise<number> {
    const id = appointment.id && appointment.id > 0 ? appointment.id : this.nextId.appointments++;
    const record: Appointment = { ...appointment, id };
    const existing = this.appointments.findIndex((a) => a.id === id);
    if (existing >= 0) this.appointments[existing] = record;
    else this.appointments.push(record);
    return id;
  }

  async updateAppointment(appointment: Appointment): Promise<void> {
    const index = this.appointments.findIndex((a) => a.id === appointment.id);
    if (index >= 0) this.appointments[index] = { ...appointment };
  }

  async deleteAppointment(appointment: Appointment): Promise<void> {
    this.appointments = this.appointments.filter((a) => a.id !== appointment.id);
  }

  // -------------------------------------------------------------- email logs

  async getAllEmailLogs(): Promise<EmailLog[]> {
    return [...this.emailLogs].sort((a, b) => b.sentTime - a.sentTime);
  }

  async insertEmailLog(log: NewEmailLog): Promise<number> {
    const id = log.id && log.id > 0 ? log.id : this.nextId.emailLogs++;
    const record: EmailLog = { ...log, id };
    const existing = this.emailLogs.findIndex((l) => l.id === id);
    if (existing >= 0) this.emailLogs[existing] = record;
    else this.emailLogs.push(record);
    return id;
  }
}
