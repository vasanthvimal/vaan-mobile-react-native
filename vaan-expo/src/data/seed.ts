import type { AppRepository } from './repository';

/**
 * Port of `AppViewModel.seedInitialDataIfNecessary()`.
 *
 * Content, ordering and relative timestamps are reproduced exactly so the
 * first-run dashboard is pixel-identical to the Kotlin app.
 */

const ONE_HOUR = 3_600_000;
const ONE_DAY = 86_400_000;

export async function seedInitialDataIfNecessary(
  repository: AppRepository,
  now: number = Date.now(),
): Promise<boolean> {
  const snapshot = await repository.refresh();

  if (
    snapshot.meetings.length > 0 ||
    snapshot.inquiries.length > 0 ||
    snapshot.appointments.length > 0
  ) {
    return false;
  }

  // 1. Seed meetings
  await repository.insertMeeting({
    clientName: 'Sarah Jenkins',
    clientEmail: 's.jenkins@acmehealth.com',
    title: 'AWS Serverless Scaling Discovery',
    description: 'Architectural review of serverless transition for health records APIs.',
    dateTime: now + ONE_HOUR * 4,
    status: 'Scheduled',
    meetingLink: 'https://meet.google.com/abc-defg-hij',
    notificationSent: true,
  });
  await repository.insertMeeting({
    clientName: 'David Chen',
    clientEmail: 'dchen@zenithretail.co',
    title: 'Data Platform Architecture & Strategy Alignment',
    description: 'Reviewing Snowflake ingestion and syncing schedules with engineering teams.',
    dateTime: now + ONE_DAY * 2,
    status: 'Scheduled',
    meetingLink: 'https://meet.google.com/xyz-qprs-tuv',
    notificationSent: true,
  });

  // 2. Seed inquiries
  await repository.insertInquiry({
    clientName: 'Elena Rostova',
    clientEmail: 'elena.r@innovatefintech.io',
    companyName: 'Innovate Fintech Ltd',
    subject: 'Kubernetes Clustering Setup & Staff Augmentation',
    message:
      'We need an external cloud engineering partner to help us refactor our legacy banking stack into modular microservices using AWS EKS. Do you have available Kubernetes experts for a 3-month contract?',
    receivedTime: now - ONE_HOUR * 3,
    status: 'New',
    replyMessage: null,
  });
  await repository.insertInquiry({
    clientName: 'Marcus Brody',
    clientEmail: 'mbrody@nexustransit.org',
    companyName: 'Nexus Transit Systems',
    subject: 'Legacy Database Migration Assessment',
    message:
      'Seeking consulting advice on migrating our mainframe DB2 schemas to PostgreSQL in GCP with zero-downtime replications.',
    receivedTime: now - ONE_DAY,
    status: 'Replied',
    replyMessage:
      'Dear Marcus, Vaan Consulting would be delighted to perform a DB migration assessment. We recommend GCP Database Migration Service (DMS) for PostgreSQL targets...',
  });

  // 3. Seed appointments
  await repository.insertAppointment({
    clientName: 'Robert Vance',
    clientEmail: 'rvance@vancerefrigeration.com',
    serviceType: 'Cloud Architecture',
    dateTime: now + ONE_DAY * 3 + ONE_HOUR * 2,
    durationMinutes: 45,
    notes: 'Migration of warehouse inventory tracking database to Azure SQL.',
    status: 'Confirmed',
    isEmailSent: true,
  });
  await repository.insertAppointment({
    clientName: 'Olivia Vance',
    clientEmail: 'olivia@vancerefrigeration.com',
    serviceType: 'AI Transformation',
    dateTime: now + ONE_DAY * 5,
    durationMinutes: 60,
    notes: 'Explore Gemini models to parse client support inquiries and automate replies.',
    status: 'Pending',
    isEmailSent: false,
  });

  // 4. Seed initial email logs
  await repository.insertEmailLog({
    recipient: 's.jenkins@acmehealth.com',
    subject: 'Confirmed: Vaan Consulting Scheduled Meeting - AWS Serverless Scaling Discovery',
    body: 'Pre-filled schedule confirmation message sent.',
    sentTime: now - ONE_HOUR * 2,
    triggerEvent: 'Meeting Scheduled',
    status: 'Sent',
  });
  await repository.insertEmailLog({
    recipient: 'rvance@vancerefrigeration.com',
    subject: 'CONFIRMED: Vaan Technical Discovery - Cloud Architecture',
    body: 'Vance Refrigeration AWS setup confirmation dispatch.',
    sentTime: now - ONE_HOUR,
    triggerEvent: 'Appointment Confirmed',
    status: 'Sent',
  });

  return true;
}
