# Dependency Replacement Report

## Build and platform

- Android Gradle project -> Expo SDK 57 / React Native 0.86.2 / React 19.2
- Kotlin/JVM + AGP stack replaced by Node/Metro/Babel/TypeScript stack

## UI and framework mapping

- Jetpack Compose Material 3 -> React Native components + custom design primitives
- Compose gradients/drawBehind/brush -> `expo-linear-gradient` + `react-native-svg`
- Compose Icons -> `@expo/vector-icons` (MaterialIcons glyph parity)

## Navigation

- Compose in-screen tab state -> React Native in-screen tab state (custom bottom bar)

## Persistence and local data

- Room (`androidx.room`) -> `expo-sqlite`
- Room Flow streams -> repository observers + Zustand selectors
- SharedPreferences bookmarks -> `expo-sqlite/kv-store`

## Networking

- OkHttp + Retrofit/Moshi style calls -> typed `fetch` wrapper (`src/services/http.ts`)

## AI integration

- Firebase AI/Gemini usage in Kotlin helper -> direct Gemini REST call or secure proxy (`VAAN_AI_PROXY_URL`)
- Added secure recommendation to keep key server-side

## Notifications

- Android NotificationCompat channel/notify -> `expo-notifications`

## Testing

- JUnit/Robolectric/Compose tests -> Jest + `jest-expo` + `@testing-library/react-native`

## Remaining optional enhancements

- Introduce React Navigation if route-level deep links are required in future.
- Replace in-code portal tabs with navigator nesting for advanced analytics and deep linking.
