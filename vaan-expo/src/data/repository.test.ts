import { InMemoryAppDao } from './inMemoryDao';
import { AppRepository } from './repository';

describe('AppRepository + InMemoryAppDao', () => {
  it('supports CRUD and sorted retrieval parity with DAO queries', async () => {
    const repo = new AppRepository(new InMemoryAppDao());

    await repo.insertMeeting({
      clientName: 'B',
      clientEmail: 'b@example.com',
      title: 'Later',
      description: 'desc',
      dateTime: 2,
      status: 'Scheduled',
      meetingLink: 'https://meet.google.com/x',
      notificationSent: false,
    });
    const firstId = await repo.insertMeeting({
      clientName: 'A',
      clientEmail: 'a@example.com',
      title: 'Earlier',
      description: 'desc',
      dateTime: 1,
      status: 'Scheduled',
      meetingLink: 'https://meet.google.com/y',
      notificationSent: false,
    });

    let snapshot = repo.getSnapshot();
    expect(snapshot.meetings.map((m) => m.title)).toEqual(['Earlier', 'Later']);

    await repo.updateMeeting({ ...snapshot.meetings[0]!, status: 'Cancelled' });
    snapshot = repo.getSnapshot();
    expect(snapshot.meetings[0]?.status).toBe('Cancelled');

    await repo.deleteMeetingById(firstId);
    snapshot = repo.getSnapshot();
    expect(snapshot.meetings).toHaveLength(1);
  });
});
