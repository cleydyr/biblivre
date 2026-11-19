package biblivre.digitalmedia;

import java.time.Instant;

/** A {@code digital_media} row: what was stored, under which name, and where its bytes are. */
public record StoredMedia(
        int id,
        String name,
        StorageLocation location,
        String contentType,
        long size,
        Instant created) {}
