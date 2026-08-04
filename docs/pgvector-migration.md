# Migrating plain PostgreSQL to vector search (pgvector)

Biblivre’s intelligent search needs the PostgreSQL **`vector`** extension (pgvector). The application does **not** require the `pgvector/pgvector` Docker image specifically—only that the extension binaries are installed on the server and can be loaded with:

```sql
CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public;
```

On application startup, migrations create that extension (global) and, per library schema, the `biblio_record_search` table with `embedding public.vector(1024)`, a GIN index on `tsv`, and an HNSW index on `embedding` (`vector_cosine_ops`). The target dimension after update `v6_0_0$9_0_2$alpha` is **1024** (default model `bge-m3`).

```text
Postgres without pgvector
  → install pgvector binaries
  → deploy Biblivre (startup updates)
  → configure embedding service
  → reindex bibliographic records
  → intelligent search ready
```

## Infrastructure paths

### A) Managed PostgreSQL (RDS, Cloud SQL, Azure, etc.)

1. Confirm the provider offers **pgvector** for your Postgres major version.
2. Enable the extension per the provider’s docs (parameter group, flag, or SQL).
3. Ensure the app role can run `CREATE EXTENSION`, **or** pre-create it as a superuser:

   ```sql
   CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public;
   ```

4. No data migration to another Docker image is required.

### B) Self-hosted PostgreSQL (OS packages)

1. Install the pgvector package matching your major version (for example `postgresql-16-pgvector`), or [build pgvector](https://github.com/pgvector/pgvector) against the same major.
2. Restart PostgreSQL if the packaging requires it.
3. Prefer pre-creating the extension as a superuser, or grant the app role permission to create it.
4. Continue with application deploy and reindex below.

### C) Docker: `postgres` image → `pgvector/pgvector`

Use this only when you cannot install the extension into the current image.

1. While the old container is still running, take a full backup:

   ```bash
   docker exec <old-db> pg_dumpall -U biblivre > biblivre-pre-pgvector.dump.sql
   ```

2. Stop the stack. Do **not** delete the old volume until restore is validated.
3. Switch the image to `pgvector/pgvector:pgXX` (same major as the current Postgres when possible).
4. For PostgreSQL **18+**, mount the data volume at `/var/lib/postgresql` (not `/var/lib/postgresql/data`). See [docker-compose.dev.yml](../docker-compose.dev.yml).
5. Start the new container with a **new/empty** volume and restore:

   ```bash
   docker exec -i <new-db> psql -U biblivre -d biblivre4 < biblivre-pre-pgvector.dump.sql
   ```

6. Validate `CREATE EXTENSION vector` and application schemas.

Avoid ad-hoc `pg_upgrade` across majors combined with the PG 18 data-directory layout change without a rehearsal. Dump/restore is the safer path for Biblivre.

## Application deploy

1. Deploy a build that includes updates `v6_0_0$9_0_0$alpha` through `v6_0_0$9_0_2$alpha`.
2. On boot the updater:
   - globally runs `CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public`;
   - per schema creates `biblio_record_search` and its indexes.
3. If the extension binaries are missing, the migration fails, rolls back, logs an error, and does **not** mark the version as installed. The app may still start, but intelligent search stays broken until the extension is installed and the app is restarted.
4. Verify per schema:

   ```sql
   SELECT extname FROM pg_extension WHERE extname = 'vector';

   SET search_path TO <schema>, public;
   \d biblio_record_search
   -- embedding should be vector(1024)
   ```

## Embedding configuration

Configure via `application.yml` or production overrides under `biblivre.search.intelligent`:

| Property | Example | Notes |
|---|---|---|
| `enabled` | `true` | Feature switch |
| `embedding.provider` | `openai_compatible` | OpenAI-compatible HTTP API |
| `embedding.base-url` | `http://embeddings-host:11434/v1` | e.g. Ollama OpenAI endpoint |
| `embedding.api-key` | `ollama` | Sent as Bearer token |
| `embedding.model` | `bge-m3` | Must match deployed model |
| `embedding.dimensions` | `1024` | Must match column and model output |

Without a reachable embedding service, reindex can still create rows with `embedding = NULL` (lexical/FTS only). With the service available, vectors are populated.

## Reindex

After migrations succeed and the embedding service is up:

1. In the UI: **Administration → Maintenance → reindex bibliographic**, or call module `administration.indexing` action `reindex` with `record_type=biblio`.
2. Validate:

   ```sql
   SET search_path TO <schema>, public;

   SELECT COUNT(*) AS total,
          COUNT(embedding) AS with_vec,
          model_id
   FROM biblio_record_search
   GROUP BY model_id;
   ```

## Production checklist

- [ ] pgvector available for the production Postgres major version
- [ ] Extension `vector` in schema `public` (pre-created or app role allowed)
- [ ] Full database backup before deploy
- [ ] App updates `9_0_0`–`9_0_2` applied on **all** library schemas
- [ ] Embedding service reachable from the app; model dims match `1024` (or column + config updated together)
- [ ] Production config has no developer-only host URLs
- [ ] Bibliographic reindex finished; `COUNT(embedding)` consistent with the collection
- [ ] UI feature flag for intelligent search enabled if the SPA should expose the mode
- [ ] Rollback plan: set `biblivre.search.intelligent.enabled=false` and/or revert the app deploy; leaving the extension/table in place does not break classic search

## Local development note

`make db-start` / `make db-reset` use [docker-compose.dev.yml](../docker-compose.dev.yml) with `pgvector/pgvector:pg18`. If the container fails to start after a Postgres 18 image change, ensure the named volume is mounted at `/var/lib/postgresql`. A destructive local reset is:

```bash
make db-reset
```
