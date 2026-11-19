package biblivre.digitalmedia;

import biblivre.core.AbstractDAO;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.exceptions.DAOException;
import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObjectManager;

public abstract class BaseDigitalMediaDAO extends AbstractDAO implements DigitalMediaDAO {

    // Rows written before storage_key existed resolve through their large object id.
    private static final String STORAGE_KEY_COLUMN =
            "COALESCE(storage_key, blob::text) AS storage_key";

    /** Picks where a new file of the given library goes, before any byte is written. */
    protected abstract StorageLocation allocate(String schema);

    protected abstract BiblivreFile getFile(StorageLocation location) throws Exception;

    protected abstract void persist(InputStream is, StorageLocation location, long size)
            throws Exception;

    protected abstract void deleteBlob(StorageLocation location);

    protected BaseDigitalMediaDAO() {
        super();
    }

    @Override
    public final Integer save(MemoryFile file) {
        try (InputStream is = file.getInputStream()) {
            Integer serial = file.getId();
            if (serial == null) {
                serial = this.getNextSerial("digital_media_id_seq");
                file.setId(serial);
            }

            if (serial != 0) {
                StorageLocation location = allocate(SchemaThreadLocal.get());

                persist(is, location, file.getSize());

                try (Connection con = datasource.getConnection();
                        PreparedStatement pst =
                                con.prepareStatement(
                                        "INSERT INTO digital_media"
                                                + " (id, name, blob, storage_key, content_type, size)"
                                                + " VALUES (?, ?, ?, ?, ?, ?);")) {
                    pst.setInt(1, serial);
                    pst.setString(2, file.getName());
                    setBlob(pst, 3, location);
                    pst.setString(4, location.storageKey());
                    pst.setString(5, file.getContentType());
                    pst.setLong(6, file.getSize());

                    pst.executeUpdate();
                    file.close();
                }
            }

            return serial;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public long createOID() {
        return withTransactionContext(
                con -> {
                    con.setAutoCommit(false);

                    PGConnection pgcon = con.unwrap(PGConnection.class);

                    LargeObjectManager lobj = pgcon.getLargeObjectAPI();
                    long oid = lobj.createLO();

                    this.commit(con);

                    return oid;
                });
    }

    @Override
    public final StorageLocation importFile(File file, String schema) {
        try (InputStream is = Files.newInputStream(file.toPath())) {
            StorageLocation location = allocate(schema);

            persist(is, location, file.length());

            return location;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public final Optional<StoredMedia> find(int id, String name) {
        try (Connection con = datasource.getConnection();
                PreparedStatement pst =
                        con.prepareStatement(
                                "SELECT id, name, blob, "
                                        + STORAGE_KEY_COLUMN
                                        + ", content_type, size, created FROM digital_media"
                                        + " WHERE id = ? AND name = ?;")) {
            // We check both ID and FILE_NAME for security reasons, so users can't "guess"
            // id's and get the files.
            pst.setInt(1, id);
            pst.setString(2, name);

            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                Timestamp created = rs.getTimestamp("created");

                return Optional.of(
                        new StoredMedia(
                                rs.getInt("id"),
                                rs.getString("name"),
                                location(rs),
                                rs.getString("content_type"),
                                rs.getLong("size"),
                                created != null ? created.toInstant() : Instant.EPOCH));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public final BiblivreFile load(int id, String name) {
        Optional<StoredMedia> media = find(id, name);

        if (media.isEmpty()) {
            return null;
        }

        StoredMedia stored = media.get();

        try {
            BiblivreFile file = getFile(stored.location());

            file.setName(stored.name());
            file.setContentType(stored.contentType());
            file.setLastModified(stored.created().toEpochMilli());
            file.setSize(stored.size());

            return file;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = datasource.getConnection();
                PreparedStatement select =
                        con.prepareStatement(
                                "SELECT blob, "
                                        + STORAGE_KEY_COLUMN
                                        + " FROM digital_media WHERE id = ?")) {
            select.setInt(1, id);

            StorageLocation location;

            try (ResultSet rs = select.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                location = location(rs);
            }

            int deleted;

            try (PreparedStatement pst =
                    con.prepareStatement("DELETE FROM digital_media WHERE id = ?;")) {
                pst.setInt(1, id);

                deleted = pst.executeUpdate();
            }

            if (location.storageKey() != null) {
                deleteBlob(location);
            }

            return deleted > 0;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<DigitalMediaDTO> list() {
        List<DigitalMediaDTO> list = new ArrayList<>();

        try (Connection con = datasource.getConnection();
                Statement st = con.createStatement();
                ResultSet rs =
                        st.executeQuery(
                                "SELECT id, blob, "
                                        + STORAGE_KEY_COLUMN
                                        + ", name FROM digital_media;")) {
            while (rs.next()) {
                DigitalMediaDTO dto = new DigitalMediaDTO();
                dto.setId(rs.getInt("id"));
                dto.setBlob(location(rs).blob());
                dto.setStorageKey(rs.getString("storage_key"));
                dto.setName(rs.getString("name"));
                list.add(dto);
            }

            return list;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    static void setBlob(PreparedStatement pst, int index, StorageLocation location)
            throws SQLException {
        if (location.blob() != null) {
            pst.setLong(index, location.blob());
        } else {
            pst.setNull(index, Types.BIGINT);
        }
    }

    private static StorageLocation location(ResultSet rs) throws SQLException {
        long blob = rs.getLong("blob");
        Long nullableBlob = rs.wasNull() ? null : blob;

        return new StorageLocation(nullableBlob, rs.getString("storage_key"));
    }
}
