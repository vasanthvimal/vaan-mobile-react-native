import type { ExpoConfig, ConfigContext } from 'expo/config';

/**
 * Dynamic Expo config.
 *
 * Migration note (secrets): the original Android app injected `GEMINI_API_KEY`
 * into `BuildConfig` via the Secrets Gradle Plugin, which bakes the key into the
 * shipped APK. We keep the same developer ergonomics (an `.env` file) but:
 *   - the key is optional; when absent the app uses the identical offline fallback
 *     copy the Kotlin app used, so UX is unchanged;
 *   - a `VAAN_AI_PROXY_URL` may be supplied instead, which keeps the key server side.
 * See docs/SECURITY_ASSESSMENT.md.
 */
export default ({ config }: ConfigContext): ExpoConfig => ({
  ...config,
  name: 'Vaan',
  slug: 'vaan-consulting',
  version: '1.0.0',
  orientation: 'portrait',
  icon: './assets/icon.jpg',
  scheme: 'vaanconsulting',
  userInterfaceStyle: 'dark',
  backgroundColor: '#0F172A',
  newArchEnabled: true,
  splash: {
    image: './assets/icon.jpg',
    resizeMode: 'contain',
    backgroundColor: '#0F172A',
  },
  assetBundlePatterns: ['**/*'],
  ios: {
    supportsTablet: true,
    bundleIdentifier: 'com.aistudio.vaanconsulting.vscqwe',
    buildNumber: '1',
    infoPlist: {
      ITSAppUsesNonExemptEncryption: false,
      UIViewControllerBasedStatusBarAppearance: false,
      NSAppTransportSecurity: {
        // No arbitrary loads: every endpoint the app talks to is HTTPS.
        NSAllowsArbitraryLoads: false,
      },
    },
  },
  android: {
    package: 'com.aistudio.vaanconsulting.vscqwe',
    versionCode: 1,
    adaptiveIcon: {
      foregroundImage: './assets/adaptive-icon.jpg',
      backgroundColor: '#0F172A',
    },
    permissions: ['android.permission.INTERNET', 'android.permission.POST_NOTIFICATIONS'],
    edgeToEdgeEnabled: true,
  },
  plugins: [
    [
      'expo-splash-screen',
      {
        image: './assets/icon.jpg',
        imageWidth: 160,
        resizeMode: 'contain',
        backgroundColor: '#0F172A',
      },
    ],
    [
      'expo-notifications',
      {
        color: '#2DD4BF',
      },
    ],
    'expo-sqlite',
  ],
  experiments: {
    typedRoutes: false,
    reactCompiler: true,
  },
  extra: {
    /**
     * Never commit a real key. Populate at build time:
     *   GEMINI_API_KEY=... npx expo run:android
     * or configure an EAS secret of the same name.
     */
    geminiApiKey: process.env.GEMINI_API_KEY ?? '',
    /** Preferred: a server-side proxy that holds the key. */
    aiProxyUrl: process.env.VAAN_AI_PROXY_URL ?? '',
    formspreeEndpoint:
      process.env.VAAN_FORMSPREE_ENDPOINT ?? 'https://formspree.io/f/mvzjrrjj',
    eas: {
      projectId: process.env.EAS_PROJECT_ID ?? '',
    },
  },
});
