import { Platform, type TextStyle } from 'react-native';

/**
 * Typography ported from `app/src/main/java/com/example/ui/theme/Type.kt`.
 *
 * Compose `FontFamily.SansSerif` resolves to the platform sans-serif (Roboto on
 * Android). Leaving `fontFamily` undefined in React Native yields the same
 * platform default, so no custom font is bundled — this keeps the bundle small
 * and the rendering identical on Android while giving iOS its native SF text.
 *
 * Compose `sp` maps directly to React Native `fontSize` (both honour the user's
 * font-scale accessibility setting), and Compose `lineHeight`/`letterSpacing`
 * map to the same-named React Native style props.
 */

export const FontWeights = {
  normal: '400',
  medium: '500',
  semiBold: '600',
  bold: '700',
  extraBold: '800',
  black: '900',
} as const satisfies Record<string, TextStyle['fontWeight']>;

/**
 * Android renders numeric `fontWeight` above 700 by synthesising the weight from
 * Roboto's variable axis; iOS maps to the matching SF face. No extra work needed,
 * but we centralise it so a future custom font swap is a one-line change.
 */
export const fontFamily = Platform.select<TextStyle['fontFamily']>({
  android: undefined,
  ios: undefined,
  default: undefined,
});

export const typography = {
  displayLarge: {
    fontFamily,
    fontWeight: FontWeights.extraBold,
    fontSize: 32,
    lineHeight: 40,
    letterSpacing: -0.5,
  },
  titleLarge: {
    fontFamily,
    fontWeight: FontWeights.bold,
    fontSize: 22,
    lineHeight: 28,
    letterSpacing: 0,
  },
  titleMedium: {
    fontFamily,
    fontWeight: FontWeights.semiBold,
    fontSize: 18,
    lineHeight: 24,
    letterSpacing: 0.15,
  },
  titleSmall: {
    fontFamily,
    fontWeight: FontWeights.medium,
    fontSize: 14,
    lineHeight: 20,
    letterSpacing: 0.1,
  },
  bodyLarge: {
    fontFamily,
    fontWeight: FontWeights.normal,
    fontSize: 16,
    lineHeight: 24,
    letterSpacing: 0.5,
  },
  bodyMedium: {
    fontFamily,
    fontWeight: FontWeights.normal,
    fontSize: 14,
    lineHeight: 20,
    letterSpacing: 0.25,
  },
  bodySmall: {
    fontFamily,
    fontWeight: FontWeights.normal,
    fontSize: 12,
    lineHeight: 16,
    letterSpacing: 0.4,
  },
  labelLarge: {
    fontFamily,
    fontWeight: FontWeights.semiBold,
    fontSize: 14,
    lineHeight: 20,
    letterSpacing: 0.1,
  },
  labelSmall: {
    fontFamily,
    fontWeight: FontWeights.bold,
    fontSize: 11,
    lineHeight: 16,
    letterSpacing: 0.5,
  },
} as const satisfies Record<string, TextStyle>;

export type TypographyToken = keyof typeof typography;
