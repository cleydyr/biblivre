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
package biblivre.core.translations;

import biblivre.core.AbstractDAO;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import biblivre.core.utils.Constants;

public class LanguagesDAO extends AbstractDAO {

    public static LanguagesDAO  getInstance() {
        return (LanguagesDAO) AbstractDAO.getInstance(LanguagesDAO.class);
    }

    public Set<LanguageDTO> list() {
        Set<LanguageDTO> set = new HashSet<>();

        Connection con = null;
        try {
            con = this.getConnection();
            StringBuilder sql = new StringBuilder();

            sql.append(
                    "SELECT language, text as name FROM global.translations WHERE key = 'language_name' ");

            if (!SchemaThreadLocal.get().equals(Constants.GLOBAL_SCHEMA)) {
                sql.append("UNION ");
                sql.append(
                        "SELECT language, text as name FROM translations WHERE key = 'language_name' ");
            }

            sql.append("ORDER BY name;");

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql.toString());

            while (rs.next()) {
                try {
                    set.add(this.populateDTO(rs));
                } catch (Exception e) {
                    this.logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }

        return set;
    }

    private LanguageDTO populateDTO(ResultSet rs) throws SQLException {
        LanguageDTO dto = new LanguageDTO();

        dto.setLanguage(rs.getString("language"));
        dto.setName(rs.getString("name"));

        return dto;
    }
}
