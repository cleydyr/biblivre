/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser  útil,
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.digitalmedia;

import biblivre.core.AbstractBO;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;
import biblivre.core.utils.Constants;
import biblivre.core.utils.FileIOUtils;
import jakarta.annotation.Nonnull;
import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DigitalMediaBO extends AbstractBO {
    /** How long a signed-but-unconfirmed upload is kept before the sweeper drops it. */
    static final Duration PENDING_UPLOAD_TTL = Duration.ofHours(24);

    static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private static final Pattern MEDIA_TYPE =
            Pattern.compile("^[A-Za-z0-9][A-Za-z0-9!#$&^_.+-]*/[A-Za-z0-9][A-Za-z0-9!#$&^_.+-]*$");

    protected DigitalMediaDAO digitalMediaDAO;

    private PendingUploadDAO pendingUploadDAO;

    private DirectMediaStore directMediaStore;

    public Integer save(MemoryFile file) {
        return this.digitalMediaDAO.save(file);
    }

    public BiblivreFile load(int id, String name) {
        return this.digitalMediaDAO.load(id, name);
    }

    public void delete(Integer fileId, String fileName) {
        this.digitalMediaDAO.delete(fileId);
    }

    public List<DigitalMediaDTO> list() {
        return this.digitalMediaDAO.list();
    }

    public StorageLocation importFile(File file, String schema) {
        return this.digitalMediaDAO.importFile(file, schema);
    }

    public Optional<BiblivreFile> parseFromBase64(String base64) {
        return findByEncodedId(base64).map(media -> digitalMediaDAO.load(media.id(), media.name()));
    }

    /**
     * Answers a request for {@code DigitalMediaController/?id=}. A store the browser can reach gets
     * a redirect to a signed URL carrying the same Content-Type and Content-Disposition Tomcat
     * would have sent; any other store streams the bytes.
     *
     * @param acceptHeader the request's Accept header, which decides inline versus attachment
     */
    public Optional<MediaDownload> resolveDownload(String encodedId, String acceptHeader) {
        Optional<StoredMedia> media = findByEncodedId(encodedId);

        if (directMediaStore == null) {
            return media.map(found -> digitalMediaDAO.load(found.id(), found.name()))
                    .map(MediaDownload.Streamed::new);
        }

        return media.map(
                found -> {
                    String contentType =
                            StringUtils.defaultIfBlank(found.contentType(), DEFAULT_CONTENT_TYPE);

                    URI location =
                            directMediaStore.presignDownload(
                                    found.location(),
                                    FileIOUtils.responseContentType(contentType),
                                    FileIOUtils.contentDisposition(
                                            contentType, found.name(), acceptHeader));

                    return new MediaDownload.Redirected(location);
                });
    }

    /** Whether the browser should send files straight to the store instead of through Tomcat. */
    public boolean supportsDirectUpload() {
        return directMediaStore != null;
    }

    /**
     * Records a pending upload and signs the PUT the browser sends the bytes with. The server
     * chooses the key; the URL only accepts the declared type and size.
     */
    public PreparedUpload prepareUpload(String name, String contentType, long size, int userId) {
        DirectMediaStore store = requireDirectStore();

        if (StringUtils.isBlank(name) || size <= 0 || size > DirectMediaStore.MAX_UPLOAD_SIZE) {
            throw new DirectUploadException("digitalmedia.error.file_could_not_be_saved");
        }

        String type = StringUtils.defaultIfBlank(contentType, DEFAULT_CONTENT_TYPE).trim();

        if (!MEDIA_TYPE.matcher(type).matches()) {
            throw new DirectUploadException("digitalmedia.error.file_could_not_be_saved");
        }

        PendingUpload upload =
                new PendingUpload(
                        UUID.randomUUID(),
                        store.newStorageKey(SchemaThreadLocal.get()),
                        // The public id is "id:name"; a colon in the name would break decoding.
                        name.replace(':', '_'),
                        type,
                        size,
                        userId);

        pendingUploadDAO.insert(upload, PENDING_UPLOAD_TTL);

        DirectMediaStore.PresignedUpload presigned =
                store.presignUpload(upload.storageKey(), type, size);

        return new PreparedUpload(upload.id(), presigned.url(), type, presigned.expiresAt());
    }

    /**
     * Confirms the object the browser sent and only then creates the {@code digital_media} row. An
     * object of the wrong size or type is discarded along with its pending upload.
     *
     * @return the encoded id that {@code DigitalMediaController/?id=} resolves
     */
    public String completeUpload(UUID uploadId, int userId) {
        DirectMediaStore store = requireDirectStore();

        PendingUpload upload =
                pendingUploadDAO
                        .findActive(uploadId)
                        .filter(found -> found.createdBy() == userId)
                        .orElseThrow(
                                () ->
                                        new DirectUploadException(
                                                "digitalmedia.error.file_not_found"));

        Optional<DirectMediaStore.StoredObject> stored = store.head(upload.storageKey());

        if (stored.isEmpty()) {
            // The PUT may not have happened; the sweeper drops the pending upload once it expires.
            throw new DirectUploadException("digitalmedia.error.no_file_uploaded");
        }

        if (!matches(upload, stored.get())) {
            log.warn(
                    "Discarding upload {}: store holds {} bytes of {}, expected {} bytes of {}",
                    upload.id(),
                    stored.get().size(),
                    stored.get().contentType(),
                    upload.size(),
                    upload.contentType());

            store.deleteObject(upload.storageKey());
            pendingUploadDAO.delete(upload.id());

            throw new DirectUploadException("digitalmedia.error.file_could_not_be_saved");
        }

        int id =
                pendingUploadDAO
                        .complete(upload)
                        .orElseThrow(
                                () ->
                                        new DirectUploadException(
                                                "digitalmedia.error.file_not_found"));

        return DigitalMediaEncodingUtil.getEncodedId(id, upload.name());
    }

    /** Drops expired pending uploads of the current library: the object first, then the row. */
    public int sweepExpiredUploads() {
        if (directMediaStore == null) {
            return 0;
        }

        List<PendingUpload> expired = pendingUploadDAO.listExpired();

        for (PendingUpload upload : expired) {
            directMediaStore.deleteObject(upload.storageKey());
            pendingUploadDAO.delete(upload.id());
        }

        return expired.size();
    }

    private static boolean matches(PendingUpload upload, DirectMediaStore.StoredObject stored) {
        return stored.size() == upload.size()
                && upload.contentType().equalsIgnoreCase(StringUtils.trim(stored.contentType()));
    }

    private DirectMediaStore requireDirectStore() {
        if (directMediaStore == null) {
            throw new DirectUploadException("digitalmedia.error.file_could_not_be_saved");
        }

        return directMediaStore;
    }

    private Optional<StoredMedia> findByEncodedId(String encodedId) {
        return findWithEncoding(encodedId, Constants.WINDOWS_CHARSET)
                .or(() -> findWithEncoding(encodedId, Constants.DEFAULT_CHARSET));
    }

    private @Nonnull Optional<StoredMedia> findWithEncoding(String id, Charset charset) {
        return DigitalMediaEncodingUtil.decode(id, charset)
                .flatMap(pair -> digitalMediaDAO.find(pair.getLeft(), pair.getRight()));
    }

    @Autowired
    public void setDigitalMediaDAO(DigitalMediaDAO digitalMediaDAO) {
        this.digitalMediaDAO = digitalMediaDAO;
    }

    @Autowired
    public void setPendingUploadDAO(PendingUploadDAO pendingUploadDAO) {
        this.pendingUploadDAO = pendingUploadDAO;
    }

    /** Present only when the active store can sign URLs for the browser. */
    @Autowired(required = false)
    public void setDirectMediaStore(DirectMediaStore directMediaStore) {
        this.directMediaStore = directMediaStore;
    }

    /** What the browser needs to send the file straight to the store. */
    public record PreparedUpload(UUID uploadId, URI url, String contentType, Instant expiresAt) {}
}
