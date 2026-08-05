import React, { memo } from 'react';
import { Platform, StyleSheet, Text, View, type StyleProp, type TextStyle, type ViewStyle } from 'react-native';

import { useColors } from '@/theme/ThemeProvider';
import { typography, type TypographyToken } from '@/theme/typography';

/**
 * `androidx.compose.material3.Text` equivalent.
 *
 * Compose resolves an unset text colour from `LocalContentColor`; here the
 * default is `colorScheme.onSurface`, which is what every call site in the
 * original relied on. Typography tokens are opt-in via `variant`, mirroring
 * `style = MaterialTheme.typography.*`.
 */
export type AppTextProps = {
  children?: React.ReactNode;
  variant?: TypographyToken;
  color?: string;
  fontSize?: number;
  lineHeight?: number;
  letterSpacing?: number;
  fontWeight?: TextStyle['fontWeight'];
  textAlign?: TextStyle['textAlign'];
  numberOfLines?: number;
  /** Compose `TextOverflow.Ellipsis`. */
  ellipsize?: boolean;
  style?: StyleProp<TextStyle>;
  selectable?: boolean;
  testID?: string;
  accessibilityRole?: 'header' | 'text' | 'link' | 'none';
  onPress?: () => void;
};

export const AppText = memo(function AppText({
  children,
  variant,
  color,
  fontSize,
  lineHeight,
  letterSpacing,
  fontWeight,
  textAlign,
  numberOfLines,
  ellipsize = true,
  style,
  selectable,
  testID,
  accessibilityRole,
  onPress,
}: AppTextProps) {
  const colors = useColors();

  return (
    <Text
      testID={testID}
      selectable={selectable}
      numberOfLines={numberOfLines}
      ellipsizeMode={ellipsize ? 'tail' : undefined}
      accessibilityRole={accessibilityRole}
      onPress={onPress}
      style={[
        variant ? typography[variant] : null,
        { color: color ?? colors.onSurface },
        fontSize !== undefined && { fontSize },
        lineHeight !== undefined && { lineHeight },
        letterSpacing !== undefined && { letterSpacing },
        fontWeight !== undefined && { fontWeight },
        textAlign !== undefined && { textAlign },
        style,
      ]}
    >
      {children}
    </Text>
  );
});

/**
 * Compose `Spacer(Modifier.height(n))` / `Spacer(Modifier.width(n))`.
 * Kept as a component so ported layouts stay visually diffable against the
 * Kotlin source.
 */
export const Spacer = memo(function Spacer({ h, w }: { h?: number; w?: number }) {
  return <View style={{ height: h, width: w }} />;
});

/** `androidx.compose.material3.HorizontalDivider` */
export const HorizontalDivider = memo(function HorizontalDivider({
  color,
  thickness = StyleSheet.hairlineWidth > 1 ? StyleSheet.hairlineWidth : 1,
  style,
}: {
  color?: string;
  thickness?: number;
  style?: StyleProp<ViewStyle>;
}) {
  const colors = useColors();
  return (
    <View
      style={[{ height: thickness, backgroundColor: color ?? colors.outlineVariant }, style]}
    />
  );
});

/**
 * Material 3 elevation → React Native shadow.
 *
 * Android maps `dp` elevation directly. iOS has no elevation concept, so the
 * shadow is derived from the M3 elevation curve (`y = dp`, `blur = dp * 2`,
 * `opacity` ramping 0.18 → 0.30) which visually matches the Android rendering.
 */
export function elevationStyle(dp: number, shadowColor = '#000000'): ViewStyle {
  if (dp <= 0) return {};

  return Platform.select<ViewStyle>({
    android: { elevation: dp, shadowColor },
    default: {
      shadowColor,
      shadowOffset: { width: 0, height: dp },
      shadowRadius: dp * 2,
      shadowOpacity: Math.min(0.18 + dp * 0.01, 0.34),
    },
  });
}
