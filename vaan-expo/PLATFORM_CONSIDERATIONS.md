# Platform-specific Considerations (Android + iOS)

## Notifications

- Android: explicit notification channel `vaan_email_channel` is configured.
- iOS: local notifications also supported; permission state handled via in-app prompt flow.

## Storage

- SQLite persistence is available on both platforms via `expo-sqlite`.
- Bookmark key/value store uses `expo-sqlite/kv-store`, also cross-platform.

## Typography

- Compose SansSerif maps to platform defaults:
  - Android: Roboto
  - iOS: San Francisco
- Typography sizes/weights/line-heights are ported token-for-token.

## Visual effects

- Compose gradients and radial glows mapped to `expo-linear-gradient` + `react-native-svg`.
- Hover behavior is mapped to pointer hover where available, and press-active state on touch devices.

## Date/time formatting

- `SimpleDateFormat` patterns from Kotlin are reproduced by explicit format utilities.

## Security defaults

- HTTPS-only request validation in custom HTTP layer.
- ATS remains strict on iOS.
