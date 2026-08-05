import { buildChatHistory, extractName, extractService, extractSubject, getChatFallback } from './geminiService';

describe('geminiService helpers', () => {
  it('buildChatHistory enforces alternating user/model turns and truncates to 10', () => {
    const history = buildChatHistory([
      { sender: 'model', text: 'ignored opening model turn' },
      { sender: 'user', text: 'u1' },
      { sender: 'user', text: 'ignored duplicate user' },
      { sender: 'model', text: 'm1' },
      { sender: 'model', text: 'ignored duplicate model' },
      { sender: 'user', text: 'u2' },
      { sender: 'model', text: 'm2' },
      { sender: 'user', text: 'u3' },
    ]);

    expect(history[0]).toEqual({ role: 'user', text: 'u1' });
    expect(history[1]).toEqual({ role: 'model', text: 'm1' });
    expect(history[2]).toEqual({ role: 'user', text: 'u2' });
    expect(history[3]).toEqual({ role: 'model', text: 'm2' });
    // trailing user is dropped (current outbound message occupies that turn)
    expect(history.find((h) => h.text === 'u3')).toBeUndefined();
    expect(history.length).toBeLessThanOrEqual(10);
  });

  it('extract helpers mirror Kotlin fallbacks', () => {
    expect(extractName('client named Alice needs help')).toBe('Alice');
    expect(extractSubject('Subject: Cloud Migration\nbody')).toBe('Cloud Migration');
    expect(extractService('Need help with Cloud & Digital Transformation now')).toBe(
      'Cloud & Digital Transformation',
    );
  });

  it('chat fallback routes by keyword intent', () => {
    expect(getChatFallback('Can you help with aws cloud migration?')).toContain('multi-cloud architectures');
    expect(getChatFallback('How do I book a call?')).toContain('Bookings tab');
  });
});
