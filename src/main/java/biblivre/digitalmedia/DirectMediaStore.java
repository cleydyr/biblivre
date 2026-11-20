package biblivre.digitalmedia;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * A media store the browser can reach on its own. The server authenticates, authorizes and signs a
 * short-lived URL; the bytes then travel between the browser and the store without a servlet
 * thread. Stores that can't do this stream through Tomcat instead.
 */
public interface DirectMediaStore {

    /** How long a signed download URL stays valid. */
    Duration DOWNLOAD_URL_TTL = Duration.ofMinutes(5);

    /** How long a signed upload URL stays valid. */
    Duration UPLOAD_URL_TTL = Duration.ofMinutes(15);

    /** Largest object a single signed PUT can carry. */
    long MAX_UPLOAD_SIZE = 5L * 1024 * 1024 * 1024;

    /** A key no other upload, in any library, can collide with. */
    String newStorageKey(String schema);

    /**
     * Signs a GET for the object. The store answers with the given Content-Type and
     * Content-Disposition.
     */
    URI presignDownload(StorageLocation location, String contentType, String contentDisposition);

    /**
     * Signs a PUT bound to the key, the Content-Type and the Content-Length. The browser must send
     * exactly those headers.
     */
    PresignedUpload presignUpload(String storageKey, String contentType, long size);

    /** What the store holds under the key, or empty when there's nothing there. */
    Optional<StoredObject> head(String storageKey);

    void deleteObject(String storageKey);

    record PresignedUpload(URI url, Instant expiresAt) {}

    record StoredObject(long size, String contentType) {}
}
