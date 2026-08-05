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

export type RepositorySnapshot = {
  meetings: ClientMeeting[];
  inquiries: ClientInquiry[];
  appointments: Appointment[];
  emailLogs: EmailLog[];
};

export const emptySnapshot: RepositorySnapshot = {
  meetings: [],
  inquiries: [],
  appointments: [],
  emailLogs: [],
};

type Listener = (snapshot: RepositorySnapshot) => void;

/**
 * Port of `AppRepository.kt`.
 *
 * Room exposed `Flow<List<T>>` which the ViewModel lifted into a `StateFlow`.
 * Here the repository owns the latest snapshot and notifies subscribers after
 * each mutation — the same "single source of truth, reactive reads" contract,
 * expressed with a plain observer so it stays framework-agnostic and easily
 * unit-testable.
 *
 * Performance: a mutation refreshes only the table it touched (`refresh` takes
 * an explicit table list) and a new snapshot object is produced only for the
 * changed slice, so unrelated selectors keep referential equality and React
 * skips re-rendering those subtrees.
 */
export class AppRepository {
  private snapshot: RepositorySnapshot = emptySnapshot;
  private readonly listeners = new Set<Listener>();

  constructor(private readonly dao: AppDao) {}

  getSnapshot(): RepositorySnapshot {
    return this.snapshot;
  }

  subscribe(listener: Listener): () => void {
    this.listeners.add(listener);
    return () => {
      this.listeners.delete(listener);
    };
  }

  private emit(): void {
    for (const listener of this.listeners) listener(this.snapshot);
  }

  /** Reloads the requested tables (all of them by default) and notifies subscribers. */
  async refresh(tables: (keyof RepositorySnapshot)[] = [
    'meetings',
    'inquiries',
    'appointments',
    'emailLogs',
  ]): Promise<RepositorySnapshot> {
    const next: RepositorySnapshot = { ...this.snapshot };

    await Promise.all(
      tables.map(async (table) => {
        switch (table) {
          case 'meetings':
            next.meetings = await this.dao.getAllMeetings();
            break;
          case 'inquiries':
            next.inquiries = await this.dao.getAllInquiries();
            break;
          case 'appointments':
            next.appointments = await this.dao.getAllAppointments();
            break;
          case 'emailLogs':
            next.emailLogs = await this.dao.getAllEmailLogs();
            break;
        }
      }),
    );

    this.snapshot = next;
    this.emit();
    return next;
  }

  // ---------------------------------------------------------------- Meetings

  async insertMeeting(meeting: NewClientMeeting): Promise<number> {
    const id = await this.dao.insertMeeting(meeting);
    await this.refresh(['meetings']);
    return id;
  }

  async updateMeeting(meeting: ClientMeeting): Promise<void> {
    await this.dao.updateMeeting(meeting);
    await this.refresh(['meetings']);
  }

  async deleteMeeting(meeting: ClientMeeting): Promise<void> {
    await this.dao.deleteMeeting(meeting);
    await this.refresh(['meetings']);
  }

  async deleteMeetingById(id: number): Promise<void> {
    await this.dao.deleteMeetingById(id);
    await this.refresh(['meetings']);
  }

  // --------------------------------------------------------------- Inquiries

  async insertInquiry(inquiry: NewClientInquiry): Promise<number> {
    const id = await this.dao.insertInquiry(inquiry);
    await this.refresh(['inquiries']);
    return id;
  }

  async updateInquiry(inquiry: ClientInquiry): Promise<void> {
    await this.dao.updateInquiry(inquiry);
    await this.refresh(['inquiries']);
  }

  async deleteInquiry(inquiry: ClientInquiry): Promise<void> {
    await this.dao.deleteInquiry(inquiry);
    await this.refresh(['inquiries']);
  }

  // ------------------------------------------------------------ Appointments

  async insertAppointment(appointment: NewAppointment): Promise<number> {
    const id = await this.dao.insertAppointment(appointment);
    await this.refresh(['appointments']);
    return id;
  }

  async updateAppointment(appointment: Appointment): Promise<void> {
    await this.dao.updateAppointment(appointment);
    await this.refresh(['appointments']);
  }

  async deleteAppointment(appointment: Appointment): Promise<void> {
    await this.dao.deleteAppointment(appointment);
    await this.refresh(['appointments']);
  }

  // -------------------------------------------------------------- Email Logs

  async insertEmailLog(log: NewEmailLog): Promise<number> {
    const id = await this.dao.insertEmailLog(log);
    await this.refresh(['emailLogs']);
    return id;
  }
}
