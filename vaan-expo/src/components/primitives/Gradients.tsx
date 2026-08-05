import React, { memo, useMemo, type ReactNode } from 'react';
import { StyleSheet, View, type StyleProp, type ViewStyle } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import Svg, { Defs, RadialGradient as SvgRadialGradient, Rect, Stop } from 'react-native-svg';

import { gradientFade } from '@/theme/colors';

/**
 * React Native equivalents of the Compose `Brush` / `drawBehind` primitives that
 * the original screens rely on for their glass, glow and backlight treatments.
 *
 * Mapping reference:
 *  - `Brush.verticalGradient(colors)`    → <Gradient direction="vertical">
 *  - `Brush.horizontalGradient(colors)`  → <Gradient direction="horizontal">
 *  - `Brush.linearGradient(colors)`      → <Gradient direction="diagonal">
 *      (Compose defaults from Offset.Zero to Offset.Infinite = top-left→bottom-right)
 *  - `Brush.radialGradient(colors, …)`   → <RadialGlow>
 *  - `Modifier.border(width, brush, …)`  → <GradientBorder> (RN has no native
 *      gradient stroke, so we render the gradient as a 1-cell-thick frame)
 *  - `Modifier.drawBehind { drawRoundRect(topLeft = -2.dp, size = +4.dp) }`
 *                                        → <GlowBackdrop inset={-2}>
 */

export type GradientDirection = 'vertical' | 'horizontal' | 'diagonal';

const DIRECTIONS: Record<GradientDirection, { start: { x: number; y: number }; end: { x: number; y: number } }> = {
  vertical: { start: { x: 0, y: 0 }, end: { x: 0, y: 1 } },
  horizontal: { start: { x: 0, y: 0 }, end: { x: 1, y: 0 } },
  diagonal: { start: { x: 0, y: 0 }, end: { x: 1, y: 1 } },
};

export type GradientProps = {
  colors: string[];
  direction?: GradientDirection;
  locations?: number[];
  style?: StyleProp<ViewStyle>;
  children?: ReactNode;
  pointerEvents?: 'auto' | 'none' | 'box-none' | 'box-only';
};

export const Gradient = memo(function Gradient({
  colors,
  direction = 'vertical',
  locations,
  style,
  children,
  pointerEvents,
}: GradientProps) {
  const { start, end } = DIRECTIONS[direction];

  // Compose composites `Color.Transparent` as transparent *black*, which reads as
  // a grey halo on RN gradients. Re-tinting the transparent stop with the
  // neighbouring hue reproduces Compose's on-screen result.
  const resolved = useMemo(() => resolveTransparentStops(colors), [colors]);

  return (
    <LinearGradient
      colors={resolved as [string, string, ...string[]]}
      start={start}
      end={end}
      locations={locations as [number, number, ...number[]] | undefined}
      style={style}
      pointerEvents={pointerEvents}
    >
      {children}
    </LinearGradient>
  );
});

function resolveTransparentStops(colors: string[]): string[] {
  return colors.map((color, index) => {
    if (color !== 'transparent') return color;
    const neighbour = colors[index - 1] ?? colors[index + 1];
    return neighbour && neighbour !== 'transparent' ? gradientFade(neighbour) : 'rgba(0,0,0,0)';
  });
}

export type RadialGlowProps = {
  /** Ordered colour stops, outermost last (matches Compose's colour list order). */
  colors: string[];
  /** Fractional centre, defaults to the Compose default of the layout centre. */
  centerX?: number;
  centerY?: number;
  /** Fractional radius relative to the layout width. Compose default ≈ 0.5 (half the min side). */
  radius?: number;
  borderRadius?: number;
  style?: StyleProp<ViewStyle>;
};

/**
 * `Brush.radialGradient` equivalent. Rendered with react-native-svg because
 * expo-linear-gradient cannot express radial ramps.
 */
export const RadialGlow = memo(function RadialGlow({
  colors,
  centerX = 0.5,
  centerY = 0.5,
  radius = 0.5,
  borderRadius = 0,
  style,
}: RadialGlowProps) {
  const stops = useMemo(() => resolveTransparentStops(colors), [colors]);

  return (
    <View pointerEvents="none" style={[StyleSheet.absoluteFill, { borderRadius, overflow: 'hidden' }, style]}>
      <Svg width="100%" height="100%">
        <Defs>
          <SvgRadialGradient
            id="glow"
            cx={`${centerX * 100}%`}
            cy={`${centerY * 100}%`}
            r={`${radius * 100}%`}
            gradientUnits="userSpaceOnUse"
          >
            {stops.map((color, index) => (
              <Stop
                // eslint-disable-next-line react/no-array-index-key
                key={index}
                offset={stops.length === 1 ? 0 : index / (stops.length - 1)}
                stopColor={stripAlpha(color)}
                stopOpacity={alphaOf(color)}
              />
            ))}
          </SvgRadialGradient>
        </Defs>
        <Rect x="0" y="0" width="100%" height="100%" fill="url(#glow)" />
      </Svg>
    </View>
  );
});

function stripAlpha(color: string): string {
  const rgba = /^rgba?\(\s*([\d.]+)[,\s]+([\d.]+)[,\s]+([\d.]+)/i.exec(color);
  if (rgba) return `rgb(${rgba[1]}, ${rgba[2]}, ${rgba[3]})`;
  if (color === 'transparent') return '#000000';
  return color.length === 9 ? color.slice(0, 7) : color;
}

function alphaOf(color: string): number {
  const rgba = /^rgba\(\s*[\d.]+[,\s]+[\d.]+[,\s]+[\d.]+[,\s/]+([\d.]+)\s*\)$/i.exec(color);
  if (rgba) return Number(rgba[1]);
  if (color === 'transparent') return 0;
  if (color.length === 9) return parseInt(color.slice(7, 9), 16) / 255;
  return 1;
}

export type GradientBorderProps = {
  colors: string[];
  direction?: GradientDirection;
  width: number;
  borderRadius: number;
  /** Fill painted inside the stroke; omit for a transparent interior. */
  innerColor?: string;
  style?: StyleProp<ViewStyle>;
  innerStyle?: StyleProp<ViewStyle>;
  children?: ReactNode;
};

/**
 * `Modifier.border(width, brush, RoundedCornerShape(radius))`.
 *
 * Compose strokes the border *inside* the layout bounds, so the child is inset
 * by `width` and its corner radius reduced accordingly — reproducing the exact
 * geometry rather than an approximation.
 */
export const GradientBorder = memo(function GradientBorder({
  colors,
  direction = 'diagonal',
  width,
  borderRadius,
  innerColor,
  style,
  innerStyle,
  children,
}: GradientBorderProps) {
  return (
    <Gradient
      colors={colors}
      direction={direction}
      style={[{ borderRadius, padding: width }, style]}
    >
      <View
        style={[
          {
            borderRadius: Math.max(borderRadius - width, 0),
            backgroundColor: innerColor ?? 'transparent',
            overflow: 'hidden',
          },
          innerStyle,
        ]}
      >
        {children}
      </View>
    </Gradient>
  );
});

export type GlowBackdropProps = {
  color: string;
  /** Compose used `topLeft = Offset(-2.dp, -2.dp)`; pass `2` for that inset. */
  spread?: number;
  borderRadius: number;
};

/**
 * `Modifier.drawBehind { drawRoundRect(color, topLeft = -spread, size = +2*spread) }`
 * — the soft outer "backlight" halo used by the pillar rows and capability cards.
 *
 * The parent must not clip (`overflow: 'visible'`, the RN default) for the halo
 * to bleed outside the bounds, exactly as in Compose.
 */
export const GlowBackdrop = memo(function GlowBackdrop({
  color,
  spread = 2,
  borderRadius,
}: GlowBackdropProps) {
  return (
    <View
      pointerEvents="none"
      style={{
        position: 'absolute',
        top: -spread,
        left: -spread,
        right: -spread,
        bottom: -spread,
        borderRadius: borderRadius + spread,
        backgroundColor: color,
      }}
    />
  );
});
