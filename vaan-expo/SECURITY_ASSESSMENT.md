# Security Assessment and Remediation Summary

## Assessment scope

- Configuration and secret handling
- Local data storage
- Network communication
- Dependency posture
- Input handling and API surface

## Findings and remediations applied

### 1) Hardcoded secrets exposure risk

- Risk: mobile-bundled keys are extractable from APK/IPA.
- Remediation:
  - Added `VAAN_AI_PROXY_URL` support to keep Gemini key server-side.
  - Kept `GEMINI_API_KEY` optional for local dev only.
  - Added `.env.example` with explicit warnings.
  - No real keys checked into source.

### 2) API key leakage via URL query parameters

- Risk: query string keys may leak into proxies/logs.
- Remediation:
  - Gemini key now sent in `x-goog-api-key` header.

### 3) Insecure transport risk

- Risk: accidental plaintext endpoint configuration.
- Remediation:
  - All custom HTTP calls reject non-HTTPS URLs.
  - iOS ATS defaults hardened (`NSAllowsArbitraryLoads=false`).

### 4) SQL injection risk in persistence layer

- Risk: dynamic SQL construction can be abused.
- Remediation:
  - All SQL statements parameterized in `src/data/sqliteDao.ts`.

### 5) Data leakage in logs

- Risk: PII or payloads emitted to logs.
- Remediation:
  - Error logs are sanitized and avoid message bodies and credentials.

## Dependency vulnerability scanning

- Planned command: `npm audit --audit-level=moderate`.
- Status in this environment: blocked due package archive download restrictions.

## Additional hardening recommendations

1. Enforce proxy-only AI mode in production builds (`VAAN_AI_PROXY_URL` required, no client key).
2. Add certificate pinning on the proxy endpoint if your risk profile requires it.
3. Implement structured telemetry redaction rules before adding analytics.
4. Add CI gate for `npm audit` and SAST checks.
