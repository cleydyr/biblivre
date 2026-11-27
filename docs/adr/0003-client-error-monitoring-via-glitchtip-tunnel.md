# Client error monitoring via a GlitchTip tunnel

Browser errors in the legacy pages and the SPA went unnoticed until a library reported them. We send them to a self-hosted GlitchTip using the Sentry browser SDK, relayed through a same-origin Biblivre endpoint (`/api/v2/client-errors`) that forwards only envelopes for the configured DSN.

## Considered Options

- **Sentry SaaS** — richest features, including session replay, but costs money and sends patron-adjacent data off our infrastructure (LGPD)
- **Homegrown endpoint + Logback** — no grouping, no UI, and alerting would be ours to build
- **OpenTelemetry browser SDK and collector** — heaviest to run, and weak at grouping browser errors
- **Self-hosted GlitchTip with the Sentry SDK** (chosen) — grouping, alerts, releases, source maps and uptime checks on our infrastructure, with the most widely used browser SDK
- **Browser posts straight to GlitchTip** — blocked by some ad blockers and needs CORS/CSP changes, so we tunnel instead

## Consequences

- Monitoring is off until `BIBLIVRE_CLIENT_MONITORING_DSN` is set; only the Biblivre server needs to reach GlitchTip
- The tunnel is unauthenticated (errors on the login page matter too), so it validates the DSN, caps size and rate-limits per IP and globally
- No session replay: GlitchTip does not ingest it and the tunnel drops it. Moving to Sentry later only changes the DSN
- The legacy pages load a ~33 kB (gzip) `biblivre.monitoring.js` before jQuery, built from the SPA's npm dependencies so SDK versions stay in one place
- Tenant (`schema`) and user id are rendered into the page by the server, not reported by the browser
