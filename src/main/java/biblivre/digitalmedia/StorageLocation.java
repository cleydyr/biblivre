package biblivre.digitalmedia;

/**
 * Where a media file's bytes live. {@code storageKey} names the object in the active store; {@code
 * blob} is the PostgreSQL large object id, present only for media kept as a large object (and for
 * S3 objects stored before {@code storage_key} existed, whose key is that id).
 */
public record StorageLocation(Long blob, String storageKey) {

    public static StorageLocation largeObject(long oid) {
        return new StorageLocation(oid, String.valueOf(oid));
    }

    public static StorageLocation key(String storageKey) {
        return new StorageLocation(null, storageKey);
    }

    /** The large object id, falling back to a numeric key written before the column existed. */
    public long oid() {
        return blob != null ? blob : Long.parseLong(storageKey);
    }
}
