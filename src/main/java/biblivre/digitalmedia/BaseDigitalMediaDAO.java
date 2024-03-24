package biblivre.digitalmedia;

import biblivre.core.AbstractDAO;
import biblivre.core.PreparedStatementUtil;
import biblivre.core.exceptions.DAOException;
import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObjectManager;

public abstract class BaseDigitalMediaDAO extends AbstractDAO implements DigitalMediaDAO {

    protected abstract BiblivreFile getFile(long oid) throws Exception;

    protected abstract void persist(InputStream is, long oid, long size) throws Exception;

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
                long oid = createOID();

                persist(is, oid, file.getSize());

                try (Connection con = datasource.getConnection()) {
                    String sql =
                            "INSERT INTO digital_media (id, name, blob, content_type, size) VALUES (?, ?, ?, ?, ?);";

                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setInt(1, serial);
                    pst.setString(2, file.getName());
                    pst.setLong(3, oid);
                    pst.setString(4, file.getContentType());
                    pst.setLong(5, file.getSize());

                    pst.executeUpdate();
                    pst.close();
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
    public final long importFile(File file) {
        try (InputStream is = Files.newInputStream(file.toPath())) {
            long oid = createOID();

            persist(is, oid, file.length());

            return oid;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public final BiblivreFile load(int id, String name) {
        try (Connection con = datasource.getConnection()) {
            // We check both ID and FILE_NAME for security reasons, so users can't "guess"
            // id's and get the files.
            String sql =
                    "SELECT name, blob, content_type, size, created FROM digital_media "
                            + "WHERE id = ? AND name = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, name);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                long oid = rs.getLong("blob");

                BiblivreFile file = getFile(oid);

                file.setName(rs.getString("name"));
                file.setContentType(rs.getString("content_type"));
                file.setLastModified(rs.getTimestamp("created").getTime());
                file.setSize(rs.getLong("size"));

                return file;
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return null;
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = datasource.getConnection()) {
            PreparedStatement preparedStatement =
                    con.prepareStatement(
                            "SELECT id, blob, name " + "FROM digital_media WHERE id = ?");

            PreparedStatementUtil.setAllParameters(preparedStatement, id);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                DigitalMediaDTO dto = new DigitalMediaDTO();
                dto.setId(rs.getInt("id"));
                dto.setBlob(rs.getLong("blob"));
                dto.setName(rs.getString("name"));

                // We check both ID and FILE_NAME for security reasons, so users can't "guess"
                // id's and get the files.
                String sql = "DELETE FROM digital_media " + "WHERE id = ?;";

                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, id);

                int deleted = pst.executeUpdate();

                deleteBlob(dto.getBlob());

                // Find out if we need to check how many records were deleted from DB.
                return deleted > 0;
            }

            return false;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    protected abstract void deleteBlob(long blob);

    @Override
    public List<DigitalMediaDTO> list() {
        List<DigitalMediaDTO> list = new ArrayList<>();
        try (Connection con = datasource.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, blob, name FROM digital_media;");

            while (rs.next()) {
                DigitalMediaDTO dto = new DigitalMediaDTO();
                dto.setId(rs.getInt("id"));
                dto.setBlob(rs.getLong("blob"));
                dto.setName(rs.getString("name"));
                list.add(dto);
            }

            return list;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
}
