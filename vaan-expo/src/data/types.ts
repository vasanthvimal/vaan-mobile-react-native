/**
 * Domain models ported from `app/src/main/java/com/example/data/Entities.kt`.
 *
 * Room's `@PrimaryKey(autoGenerate = true) val id: Int = 0` becomes an optional
 * `id` on the *input* types and a required `id` on the persisted types.
 */

export type MeetingStatus = 'Scheduled' | 'Completed' | 'Cancelled';
export type InquiryStatus = 'New' | 'Under Review' | 'Replied';
export type AppointmentStatus = 'Pending' | 'Confirmed' | 'Completed';
export type EmailStatus = 'Sent' | 'Failed';

/** `@Entity(tableName = "meetings")` */
export type ClientMeeting = {
  id: number;
  clientName: string;
  clientEmail: string;
  title: string;
  description: string;
  /** Epoch millis, matching the Kotlin `Long` timestamp. */
  dateTime: number;
  status: MeetingStatus | string;
  meetingLink: string;
  notificationSent: boolean;
};

/** `@Entity(tableName = "inquiries")` */
export type ClientInquiry = {
  id: number;
  clientName: string;
  clientEmail: string;
  companyName: string;
  subject: string;
  message: string;
  receivedTime: number;
  status: InquiryStatus | string;
  replyMessage: string | null;
};

/** `@Entity(tableName = "appointments")` */
export type Appointment = {
  id: number;
  clientName: string;
  clientEmail: string;
  /** "Cloud Architecture" | "Salesforce Consulting" | "Software Engineering" | "AI Transformation" | … */
  serviceType: string;
  dateTime: number;
  durationMinutes: number;
  notes: string;
  status: AppointmentStatus | string;
  isEmailSent: boolean;
};

/** `@Entity(tableName = "email_logs")` */
export type EmailLog = {
  id: number;
  recipient: string;
  subject: string;
  body: string;
  sentTime: number;
  /** "Meeting Confirmed" | "Inquiry Reply" | "Appointment Reminder" | "Auto-Notification" | … */
  triggerEvent: string;
  status: EmailStatus | string;
};

export type NewClientMeeting = Omit<ClientMeeting, 'id'> & { id?: number };
export type NewClientInquiry = Omit<ClientInquiry, 'id'> & { id?: number };
export type NewAppointment = Omit<Appointment, 'id'> & { id?: number };
export type NewEmailLog = Omit<EmailLog, 'id'> & { id?: number };

/** Chat transcript entry — mirrors `ChatMessage` in AppViewModel.kt. */
export type ChatMessage = {
  /** "user" | "model" */
  role: 'user' | 'model';
  text: string;
};

/** Mirrors the sealed `AiDraftState` class in AppViewModel.kt. */
export type AiDraftState =
  | { kind: 'idle' }
  | { kind: 'loading' }
  | { kind: 'success'; draft: string }
  | { kind: 'error'; message: string };

export const AiDraftIdle: AiDraftState = { kind: 'idle' };
export const AiDraftLoading: AiDraftState = { kind: 'loading' };
