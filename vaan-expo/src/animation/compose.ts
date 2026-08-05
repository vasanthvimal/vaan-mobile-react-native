import { Easing, withSpring, withTiming, type WithSpringConfig, type WithTimingConfig } from 'react-native-reanimated';

/**
 * Compose animation specs translated to Reanimated equivalents.
 *
 * Compose and Reanimated both model springs as a damped harmonic oscillator with
 * `mass = 1`, so a Compose `dampingRatio (ζ)` / `stiffness (k)` pair converts
 * exactly via `damping = 2ζ√k`. That means the motion curves are numerically
 * identical, not merely similar.
 */

// androidx.compose.animation.core.Spring constants
export const Spring = {
  DampingRatioHighBouncy: 0.2,
  DampingRatioMediumBouncy: 0.5,
  DampingRatioLowBouncy: 0.75,
  DampingRatioNoBouncy: 1,
  StiffnessHigh: 10_000,
  StiffnessMedium: 1_500,
  StiffnessMediumLow: 400,
  StiffnessLow: 200,
  StiffnessVeryLow: 50,
} as const;

export function composeSpring(
  dampingRatio: number = Spring.DampingRatioNoBouncy,
  stiffness: number = Spring.StiffnessMedium,
): WithSpringConfig {
  'worklet';
  return {
    mass: 1,
    stiffness,
    damping: 2 * dampingRatio * Math.sqrt(stiffness),
    overshootClamping: false,
    restDisplacementThreshold: 0.001,
    restSpeedThreshold: 0.001,
  };
}

/** androidx.compose.animation.core easing curves. */
export const ComposeEasing = {
  /** CubicBezier(0.4, 0.0, 0.2, 1.0) — the default `tween` easing. */
  FastOutSlowIn: Easing.bezier(0.4, 0.0, 0.2, 1.0),
  /** CubicBezier(0.0, 0.0, 0.2, 1.0) */
  LinearOutSlowIn: Easing.bezier(0.0, 0.0, 0.2, 1.0),
  /** CubicBezier(0.4, 0.0, 1.0, 1.0) */
  FastOutLinearIn: Easing.bezier(0.4, 0.0, 1.0, 1.0),
  Linear: Easing.linear,
} as const;

export function composeTween(
  durationMillis: number,
  easing: WithTimingConfig['easing'] = ComposeEasing.FastOutSlowIn,
): WithTimingConfig {
  'worklet';
  return { duration: durationMillis, easing };
}

/** `animateFloatAsState(target, spring(...))` */
export function springTo(value: number, dampingRatio?: number, stiffness?: number) {
  'worklet';
  return withSpring(value, composeSpring(dampingRatio, stiffness));
}

/** `animateFloatAsState(target, tween(...))` / `animateColorAsState(...)` */
export function tweenTo(
  value: number,
  durationMillis: number,
  easing?: WithTimingConfig['easing'],
) {
  'worklet';
  return withTiming(value, composeTween(durationMillis, easing));
}
