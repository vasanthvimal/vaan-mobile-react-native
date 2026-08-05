# Vaan Expo Migration

Cross-platform React Native + Expo migration of the original Android Kotlin/Compose app.

## Prerequisites

- Node.js LTS
- npm
- Android Studio (for Android emulator)
- Xcode (for iOS simulator on macOS)

## Setup

1. Copy `.env.example` to `.env` and fill required values.
2. Install dependencies:
   - `npm install`
3. Start Metro:
   - `npm run start`

## Run

- Android: `npm run android`
- iOS: `npm run ios`

## Quality commands

- Typecheck: `npm run typecheck`
- Lint: `npm run lint`
- Unit tests: `npm run test`
- Coverage: `npm run test:coverage`
- Expo health check: `npm run doctor`

## Notes

- If `VAAN_AI_PROXY_URL` is configured, AI requests are sent to your proxy (recommended).
- If only `GEMINI_API_KEY` is configured, requests go directly to Gemini from the client.
