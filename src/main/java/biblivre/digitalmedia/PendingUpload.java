package biblivre.digitalmedia;

import java.util.UUID;

/**
 * A direct upload the server signed but hasn't confirmed. It becomes a {@code digital_media} row
 * only once the store holds an object of the promised size and type; until then nothing can be
 * attached to a record.
 */
public record PendingUpload(
        UUID id, String storageKey, String name, String contentType, long size, int createdBy) {}
