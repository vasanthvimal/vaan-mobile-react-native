# Performance Testing and Optimization Report

## Baseline optimization work completed in code

- Store architecture uses selector-based subscriptions (Zustand), reducing broad re-renders.
- Repository refresh can target changed tables instead of full reloads.
- Animation primitives are designed for UI-thread execution (Reanimated-friendly structure).
- List-heavy screens use incremental section rendering and compact cards.

## Metrics plan (to execute once dependency install is available)

### Startup and TTI

- Measure cold start and warm start on Android/iOS using native traces.
- Track JS bundle execution and first interactive frame.

### Frame rate and interaction smoothness

- Validate 60 FPS minimum on screen transitions and scrolling.
- Stress test card-heavy and chat screens.

### Memory

- Check for retained references in chat and modal flows.
- Verify no unbounded list growth in local state.

### Bundle size

- Generate production bundle stats and identify heavy dependencies.

## Commands to run

- `npm run typecheck`
- `npm run lint`
- `npm run test`
- `npm run test:coverage`
- `npx expo start --no-dev --minify`
- `npx expo-doctor`

## Status in this environment

- Not executed due blocked package archive downloads by network policy.
