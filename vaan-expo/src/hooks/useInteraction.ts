import { useCallback, useMemo, useState } from 'react';
import {
  useAnimatedStyle,
  useDerivedValue,
  useSharedValue,
  withRepeat,
  type SharedValue,
} from 'react-native-reanimated';

import { ComposeEasing, Spring, composeSpring, composeTween } from '@/animation/compose';
import { withSpring, withTiming } from 'react-native-reanimated';

/**
 * Compose's `MutableInteractionSource` + `collectIsHoveredAsState()` /
 * `collectIsPressedAsState()` expressed as a React hook.
 *
 * Hover is a genuine input state on React Native: `Pressable` emits
 * `onHoverIn`/`onHoverOut` for pointer devices (Android with a mouse/trackpad,
 * iPadOS with a trackpad, and desktop/web). On touch-only devices the pressed
 * state carries the same visual affordance, which is exactly how the Compose
 * original behaved (`isActive = isHovered || isPressed`).
 */
export type InteractionState = {
  pressed: boolean;
  hovered: boolean;
  /** Compose: `isHovered || isPressed` */
  active: boolean;
  handlers: {
    onPressIn: () => void;
    onPressOut: () => void;
    onHoverIn: () => void;
    onHoverOut: () => void;
  };
};

export function useInteraction(): InteractionState {
  const [pressed, setPressed] = useState(false);
  const [hovered, setHovered] = useState(false);

  const onPressIn = useCallback(() => setPressed(true), []);
  const onPressOut = useCallback(() => setPressed(false), []);
  const onHoverIn = useCallback(() => setHovered(true), []);
  const onHoverOut = useCallback(() => setHovered(false), []);

  const handlers = useMemo(
    () => ({ onPressIn, onPressOut, onHoverIn, onHoverOut }),
    [onPressIn, onPressOut, onHoverIn, onHoverOut],
  );

  return { pressed, hovered, active: pressed || hovered, handlers };
}

/**
 * `animateFloatAsState(if (isPressed) pressedScale else 1f, spring(...))` driven
 * entirely on the UI thread, so the press feedback stays at 60/120fps even while
 * JS is busy (a measurable improvement over the Compose original's behaviour
 * under load).
 */
export function usePressScale(
  pressed: boolean,
  pressedScale: number,
  dampingRatio: number = Spring.DampingRatioMediumBouncy,
  stiffness: number = Spring.StiffnessLow,
) {
  const progress = useSharedValue(pressed ? pressedScale : 1);

  progress.value = withSpring(pressed ? pressedScale : 1, composeSpring(dampingRatio, stiffness));

  return useAnimatedStyle(() => ({ transform: [{ scale: progress.value }] }), []);
}

/** `animateFloatAsState(target, tween(durationMillis, easing))` as a shared value. */
export function useTweenValue(
  target: number,
  durationMillis: number,
  easing = ComposeEasing.FastOutSlowIn,
): SharedValue<number> {
  const value = useSharedValue(target);
  value.value = withTiming(target, composeTween(durationMillis, easing));
  return value;
}

/**
 * `rememberInfiniteTransition().animateFloat(initial, target,
 *   infiniteRepeatable(tween(duration, LinearEasing), RepeatMode.Reverse))`
 * — the "breathing" ambient glow used by the logo and glassy buttons.
 */
export function useBreathingGlow(
  initialValue: number,
  targetValue: number,
  durationMillis: number,
): SharedValue<number> {
  const progress = useSharedValue(initialValue);

  useMemo(() => {
    progress.value = initialValue;
    progress.value = withRepeat(
      withTiming(targetValue, composeTween(durationMillis, ComposeEasing.Linear)),
      -1,
      true,
    );
    // `progress` is a stable shared value; re-arm only when the spec changes.
  }, [progress, initialValue, targetValue, durationMillis]);

  return progress;
}

/** Convenience: maps a 0..1 shared value onto an animated `opacity`. */
export function useAnimatedOpacity(value: SharedValue<number>, multiplier = 1) {
  return useAnimatedStyle(() => ({ opacity: value.value * multiplier }), [multiplier]);
}

/** Convenience: derives a scalar so worklets can read it without re-creating styles. */
export function useScalar(value: number): SharedValue<number> {
  return useDerivedValue(() => value, [value]);
}
