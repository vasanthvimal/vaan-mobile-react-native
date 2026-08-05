import {
  Cyan80,
  Navy80,
  Teal80,
  VaanCyanAccent,
  VaanMidnight,
  VaanPrimaryBlue,
  VaanSlateBg,
  VaanSlateCard,
  VaanSlateDarkCard,
  VaanTeal,
} from './colors';

/**
 * Material 3 `ColorScheme` equivalent.
 *
 * The Kotlin app declared only a handful of roles in `darkColorScheme(...)`;
 * every other role fell through to the Material 3 baseline dark tokens. Those
 * baseline values are reproduced here verbatim so that screens referencing
 * `MaterialTheme.colorScheme.onSurfaceVariant` / `surfaceVariant` /
 * `outlineVariant` render identically.
 */
export type ColorScheme = {
  primary: string;
  onPrimary: string;
  primaryContainer: string;
  onPrimaryContainer: string;
  secondary: string;
  onSecondary: string;
  secondaryContainer: string;
  onSecondaryContainer: string;
  tertiary: string;
  onTertiary: string;
  background: string;
  onBackground: string;
  surface: string;
  onSurface: string;
  surfaceVariant: string;
  onSurfaceVariant: string;
  outline: string;
  outlineVariant: string;
  error: string;
  onError: string;
  errorContainer: string;
  onErrorContainer: string;
  scrim: string;
};

/** Mirrors `DarkColorScheme` in Theme.kt + Material 3 baseline dark tokens. */
export const darkColorScheme: ColorScheme = {
  primary: Teal80,
  onPrimary: VaanMidnight,
  primaryContainer: '#4F378B', // M3 baseline (Primary30)
  onPrimaryContainer: '#EADDFF', // M3 baseline (Primary90)
  secondary: Cyan80,
  onSecondary: VaanMidnight,
  secondaryContainer: '#4A4458', // M3 baseline (Secondary30)
  onSecondaryContainer: '#E8DEF8', // M3 baseline (Secondary90)
  tertiary: Navy80,
  onTertiary: '#492532', // M3 baseline (Tertiary20)
  background: VaanMidnight,
  onBackground: '#FFFFFF',
  surface: VaanSlateDarkCard,
  onSurface: '#FFFFFF',
  surfaceVariant: '#49454F', // M3 baseline (NeutralVariant30)
  onSurfaceVariant: '#CAC4D0', // M3 baseline (NeutralVariant80)
  outline: '#938F99', // M3 baseline (NeutralVariant60)
  outlineVariant: '#49454F', // M3 baseline (NeutralVariant30)
  error: '#F2B8B5', // M3 baseline (Error80)
  onError: '#601410', // M3 baseline (Error20)
  errorContainer: '#8C1D18', // M3 baseline (Error30)
  onErrorContainer: '#F9DEDC', // M3 baseline (Error90)
  scrim: '#000000',
};

/** Mirrors `LightColorScheme` in Theme.kt. Retained for completeness/parity. */
export const lightColorScheme: ColorScheme = {
  primary: VaanPrimaryBlue,
  onPrimary: '#FFFFFF',
  primaryContainer: '#EADDFF',
  onPrimaryContainer: '#21005D',
  secondary: VaanTeal,
  onSecondary: '#FFFFFF',
  secondaryContainer: '#E8DEF8',
  onSecondaryContainer: '#1D192B',
  tertiary: VaanCyanAccent,
  onTertiary: '#FFFFFF',
  background: VaanSlateBg,
  onBackground: VaanMidnight,
  surface: VaanSlateCard,
  onSurface: VaanMidnight,
  surfaceVariant: '#E7E0EC',
  onSurfaceVariant: '#49454F',
  outline: '#79747E',
  outlineVariant: '#CAC4D0',
  error: '#B3261E',
  onError: '#FFFFFF',
  errorContainer: '#F9DEDC',
  onErrorContainer: '#410E0B',
  scrim: '#000000',
};
