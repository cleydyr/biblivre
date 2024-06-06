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
package biblivre.administration.backup;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class BackupDAOImpl extends AbstractDAO implements BackupDAO {

    @Override
    public boolean save(BackupDTO dto) {
        if (dto == null || dto.getType() == null) {
            return false;
        }

        try (Connection con = datasource.getConnection()) {

            StringBuilder sql = new StringBuilder();

            if (dto.getId() == null) {
                dto.setId(this.getNextSerial("backups_id_seq"));

                sql.append(
                        "INSERT INTO backups (id, path, schemas, type, scope, downloaded, steps, current_step) ");
                sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

                PreparedStatement pst = con.prepareStatement(sql.toString());

                pst.setInt(1, dto.getId());

                if (dto.getBackup() != null) {
                    pst.setString(2, dto.getBackup().getAbsolutePath());
                } else {
                    pst.setNull(2, Types.VARCHAR);
                }

                pst.setString(3, dto.getSchemasString());
                pst.setString(4, dto.getType().toString());
                pst.setString(5, dto.getBackupScope().toString());
                pst.setBoolean(6, dto.isDownloaded());

                if (dto.getSteps() != null) {
                    pst.setInt(7, dto.getSteps());
                } else {
                    pst.setNull(7, Types.INTEGER);
                }

                if (dto.getCurrentStep() != null) {
                    pst.setInt(8, dto.getCurrentStep());
                } else {
                    pst.setNull(8, Types.INTEGER);
                }

                return pst.executeUpdate() > 0;

            } else {
                sql.append(
                        "UPDATE backups SET path = ?, downloaded = ?, current_step = ? WHERE id = ?;");

                PreparedStatement pst = con.prepareStatement(sql.toString());

                if (dto.getBackup() != null) {
                    pst.setString(1, dto.getBackup().getAbsolutePath());
                } else {
                    pst.setNull(1, Types.VARCHAR);
                }

                pst.setBoolean(2, dto.isDownloaded());
                pst.setInt(3, dto.getCurrentStep());
                pst.setInt(4, dto.getId());

                return pst.executeUpdate() > 0;
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public BackupDTO get(Integer id) {
        if (id == null) {
            return null;
        }

        BackupDTO dto = null;

        try (Connection con = datasource.getConnection()) {

            PreparedStatement pst = con.prepareStatement("SELECT * FROM backups WHERE id = ?;");

            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                dto = this.populateDTO(rs);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return dto;
    }

    @Override
    public Set<String> listDatabaseSchemas() {
        Set<String> set = new HashSet<>();

        try (Connection con = datasource.getConnection()) {
            String sql =
                    "SELECT schema_name FROM information_schema.schemata WHERE schema_name <> 'information_schema' AND schema_name !~ E'^pg_';";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                set.add(rs.getString("schema_name"));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return set;
    }

    @Override
    public List<BackupDTO> list() {
        return this.list(0);
    }

    @Override
    public List<BackupDTO> list(int limit) {
        ArrayList<BackupDTO> list = new ArrayList<>();

        try (Connection con = datasource.getConnection()) {
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT * FROM backups ORDER BY created DESC ");

            if (limit > 0) {
                sql.append("LIMIT ?");
            }

            PreparedStatement pst = con.prepareStatement(sql.toString());
            if (limit > 0) {
                pst.setInt(1, limit);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(this.populateDTO(rs));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return list;
    }

    private BackupDTO populateDTO(ResultSet rs) throws SQLException {
        String schemas = rs.getString("schemas");
        BackupType type = BackupType.fromString(rs.getString("type"));
        BackupScope scope = BackupScope.fromString(rs.getString("scope"));

        BackupDTO dto = new BackupDTO(schemas, type, scope);

        dto.setId(rs.getInt("id"));
        dto.setCreated(rs.getTimestamp("created"));

        String path = rs.getString("path");
        if (StringUtils.isNotBlank(path)) {
            dto.setBackup(new File(path));
        }

        dto.setDownloaded(rs.getBoolean("downloaded"));
        dto.setSteps(rs.getInt("steps"));
        dto.setCurrentStep(rs.getInt("current_step"));

        return dto;
    }
}
