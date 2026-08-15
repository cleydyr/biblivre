# Bound schema header hides other libraries

A public tenant host (nginx in front of one Biblivre instance) must not reveal or serve other libraries. We honor `X-Biblivre-Bound-Schema` from the proxy: list and JSP see only that schema, a path to another schema is 403, and a missing header keeps the current multi-schema picker for local/dev and the unpublished management host.

## Considered Options

- **Hide the SPA picker only** — the public `multi_schema.list` and `/other/` still leak
- **Finish isolation in nginx** (`sub_filter` on JSON, prefix `/spa`) — breaks on the next endpoint
- **Overwrite `X-Biblivre-Schema`** — the SPA already sends that header from `localStorage`, so a path to another schema would be forced instead of 403
- **Bound header + 403 on path mismatch** (chosen) — nginx is the host→schema map; Biblivre enforces visibility and access at the request edge

## Consequences

- Nginx must set `X-Biblivre-Bound-Schema` on every public `location` (`/`, `/spa`, `/api/`, `/static/`, `/Biblivre6/`)
- `/spa` is a reserved path, not a schema name
- A stale client `X-Biblivre-Schema` is ignored when the bound header is present, so old `localStorage` does not 403 the API
- `SchemaBO.getSchemas()` stays unfiltered (backup, index, migrations)
