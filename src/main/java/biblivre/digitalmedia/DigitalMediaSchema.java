package biblivre.digitalmedia;

import java.util.List;

/**
 * DDL that gives a schema's {@code digital_media} its {@code storage_key} column and the table of
 * pending direct uploads. Every statement is idempotent, so both the schema update and a restore of
 * an older backup can run it.
 */
public final class DigitalMediaSchema {

    /** Statements that let {@code digital_media} hold media that has no large object. */
    public static final List<String> STORAGE_KEY_COLUMN =
            List.of(
                    "ALTER TABLE digital_media ADD COLUMN IF NOT EXISTS storage_key character varying",
                    "ALTER TABLE digital_media ALTER COLUMN blob DROP NOT NULL");

    /** Points every existing row at the key its store already uses: the large object id. */
    public static final String BACKFILL_STORAGE_KEY =
            "UPDATE digital_media SET storage_key = blob::text"
                    + " WHERE storage_key IS NULL AND blob IS NOT NULL";

    public static final String PENDING_UPLOAD_TABLE =
            """
            CREATE TABLE IF NOT EXISTS digital_media_pending_upload (
                id uuid NOT NULL PRIMARY KEY,
                storage_key character varying NOT NULL,
                name character varying NOT NULL,
                content_type character varying NOT NULL,
                size bigint NOT NULL,
                created timestamp without time zone DEFAULT now() NOT NULL,
                created_by integer,
                expires_at timestamp without time zone NOT NULL
            )
            """;

    public static final String PENDING_UPLOAD_EXPIRES_INDEX =
            """
            CREATE INDEX IF NOT EXISTS "IX_digital_media_pending_upload_expires_at"
            ON digital_media_pending_upload USING btree (expires_at)
            """;

    private DigitalMediaSchema() {}
}
