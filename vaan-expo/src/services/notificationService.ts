import * as Notifications from 'expo-notifications';
import { Platform } from 'react-native';

/**
 * Port of the notification code in `AppViewModel.kt`
 * (`createNotificationChannel` + `triggerAndroidSystemNotification`).
 *
 * Parity notes:
 *  - identical channel id/name/description and `IMPORTANCE_DEFAULT`;
 *  - identical title ("Vaan Auto-Email Sent") and body ("To: …\nSub: …");
 *  - `BigTextStyle` + `PRIORITY_HIGH` + `setAutoCancel(true)` map to the
 *    Expo defaults for a locally-presented notification;
 *  - like the Kotlin app we do **not** proactively request the runtime
 *    permission. When it is missing we surface the same in-app
 *    "Enable Delivery Alerts" dialog instead, so the UX is unchanged.
 *    Cross-platform note: iOS behaves the same way, whereas the original app
 *    simply had no notifications on iOS.
 */

export const NOTIFICATION_CHANNEL_ID = 'vaan_email_channel';

let handlerConfigured = false;
let channelPromise: Promise<void> | null = null;

function configureHandler(): void {
  if (handlerConfigured) return;
  handlerConfigured = true;

  Notifications.setNotificationHandler({
    handleNotification: async () => ({
      shouldShowBanner: true,
      shouldShowList: true,
      shouldPlaySound: false,
      shouldSetBadge: false,
    }),
  });
}

/** Equivalent of `createNotificationChannel()` (Android O+ only, like the original). */
export function ensureNotificationChannel(): Promise<void> {
  configureHandler();

  if (Platform.OS !== 'android') return Promise.resolve();

  channelPromise ??= Notifications.setNotificationChannelAsync(NOTIFICATION_CHANNEL_ID, {
    name: 'Vaan Email Dispatches',
    description:
      'Alerts notifying when automated email communications are triggered for clients.',
    importance: Notifications.AndroidImportance.DEFAULT,
  })
    .then(() => undefined)
    .catch((error: unknown) => {
      channelPromise = null;
      throw error;
    });

  return channelPromise;
}

export type NotificationResult = 'sent' | 'permission-denied' | 'failed';

/** Equivalent of `triggerAndroidSystemNotification(recipient, subject)`. */
export async function notifyAutomatedEmail(
  recipient: string,
  subject: string,
): Promise<NotificationResult> {
  try {
    await ensureNotificationChannel();

    const { granted } = await Notifications.getPermissionsAsync();
    if (!granted) {
      return 'permission-denied';
    }

    await Notifications.scheduleNotificationAsync({
      content: {
        title: 'Vaan Auto-Email Sent',
        body: `To: ${recipient}\nSub: ${subject}`,
        priority: Notifications.AndroidNotificationPriority.HIGH,
        autoDismiss: true,
        sound: false,
      },
      trigger: null,
    });

    return 'sent';
  } catch (error) {
    const reason = error instanceof Error ? error.name : 'UnknownError';
    console.warn(`[NotificationService] Failed to present notification (${reason}).`);
    return 'failed';
  }
}
