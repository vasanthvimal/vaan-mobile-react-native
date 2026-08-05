import React, { createContext, useContext, useMemo, type ReactNode } from 'react';

import { darkColorScheme, lightColorScheme, type ColorScheme } from './colorScheme';
import { typography } from './typography';

export type Theme = {
  colors: ColorScheme;
  typography: typeof typography;
  isDark: boolean;
};

/**
 * `MyApplicationTheme` in Theme.kt forces `darkTheme = true` and
 * `dynamicColor = false` so the brand palette is always applied. We keep the
 * same behaviour (and the same escape hatch) here.
 */
export const darkTheme: Theme = { colors: darkColorScheme, typography, isDark: true };
export const lightTheme: Theme = { colors: lightColorScheme, typography, isDark: false };

const ThemeContext = createContext<Theme>(darkTheme);

export function VaanThemeProvider({
  children,
  dark = true,
}: {
  children: ReactNode;
  dark?: boolean;
}) {
  const value = useMemo(() => (dark ? darkTheme : lightTheme), [dark]);
  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
}

export function useTheme(): Theme {
  return useContext(ThemeContext);
}

/** Shorthand for the very common `useTheme().colors`. */
export function useColors(): ColorScheme {
  return useContext(ThemeContext).colors;
}
