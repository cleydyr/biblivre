# Client error monitoring

Biblivre can report browser errors to a self-hosted [GlitchTip](https://glitchtip.com) (Sentry-compatible), so problems show up as alerts before a library calls in. It is off until a DSN is configured.

## What is reported

| Source | Captured |
|---|---|
| Legacy JSP pages | Uncaught errors and promise rejections; AJAX calls that fail with HTTP 5xx or a network error (`$(document).ajaxError` in `biblivre.core.js`) |
| SPA (`/spa`) | Uncaught errors; render errors (React `ErrorBoundary`, which shows a "reload" page); React Query queries and mutations that fail with anything but HTTP 4xx |

Not reported: `success:false` JSON answers and HTTP 4xx, which are validation or permission outcomes. Error messages shown with `Core.msg` become breadcrumbs, so they appear in the trail before a real error.

Each event carries the `schema` (library) tag, an `app` tag (`legacy` or `spa`), the logged-in user's numeric id, the release (`biblivre@<version>`), the page URL and breadcrumbs (clicks, navigation, XHR/fetch). Cookies, headers and request bodies are not collected, and query parameters whose names look like `pass`, `token`, `secret`, `key` or `auth` are replaced with `[Filtered]`. Session replay is not available: GlitchTip does not ingest replays, and the tunnel drops them.

## How it flows

```
browser (Sentry SDK) ──POST /api/v2/client-errors──► Biblivre ──► GlitchTip /api/<project>/envelope/
```

The browser never talks to GlitchTip. `ClientErrorTunnelController` accepts envelopes on the same origin (so ad blockers, CORS and CSP are not a concern), and:

- forwards only envelopes addressed to the configured DSN (403 otherwise), so it is not an open proxy;
- rejects bodies over 1 MB (413);
- limits each client IP to 60 envelopes per minute and all clients together to 1200 (429);
- drops session replay items.

Only the Biblivre server needs to reach GlitchTip, so the DSN may use an internal host name.

## Setup

1. Run GlitchTip. For local development:

   ```bash
   docker compose --profile monitoring up -d glitchtip-database glitchtip
   ```

   For production, run it on its own host following the [GlitchTip install guide](https://glitchtip.com/documentation/install), with a real `SECRET_KEY` and `EMAIL_URL`.

2. Open GlitchTip (`http://localhost:8100` locally), sign up, create an organization and a project of platform **JavaScript**, and copy its DSN.

3. Give Biblivre the DSN and restart it:

   | Variable | Default | Purpose |
   |---|---|---|
   | `BIBLIVRE_CLIENT_MONITORING_DSN` | empty (disabled) | DSN of the GlitchTip project, reachable from the Biblivre server, e.g. `http://<key>@glitchtip:8000/1` |
   | `BIBLIVRE_CLIENT_MONITORING_ENVIRONMENT` | `production` | Event environment, e.g. `staging` |
   | `BIBLIVRE_CLIENT_MONITORING_SAMPLE_RATE` | `1.0` | Fraction of errors sent |

4. In GlitchTip, under **Project settings → Alerts**, add an alert that notifies by email and/or a webhook (Slack, Discord, or any URL) when a new issue appears or an issue exceeds a number of events per minute.

5. Optional but recommended: under **Uptime monitors**, add a monitor for each library's public URL. It alerts when a site is down entirely, which no browser can report.

## Source maps for the SPA

The SPA is minified. To get readable stack traces, set these when building (`mvn package` or `yarn build`):

| Variable | Value |
|---|---|
| `SENTRY_AUTH_TOKEN` | A GlitchTip auth token with `project:releases` scope |
| `SENTRY_URL` | GlitchTip base URL, e.g. `https://glitchtip.example.org` |
| `SENTRY_ORG` / `SENTRY_PROJECT` | Organization and project slugs |

With the token set, Vite generates hidden source maps, `@sentry/vite-plugin` uploads them under the release `biblivre@<project.version>` (Maven passes it as `BIBLIVRE_RELEASE`), and then deletes the `.map` files so they are never served. Without the token, no maps are generated. The legacy scripts are not minified and need none.

## Checking it works

With monitoring enabled, open a legacy page and run in the browser console:

```js
setTimeout(() => { throw new Error('monitoring test') })
```

The browser's network tab should show `POST …/api/v2/client-errors` answering 200, and the issue should appear in GlitchTip with the `schema` tag and user id.
