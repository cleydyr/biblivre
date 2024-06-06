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
package biblivre.login;

import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.PreparedStatementUtil;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.Constants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.stereotype.Service;

@Service
public class LoginDAOImpl extends AbstractDAO implements LoginDAO {

    @Override
    public LoginDTO get(Integer loginId) {
        try (Connection con = datasource.getConnection()) {
            String sql =
                    "SELECT id, login, employee, password, password_salt, salted_password FROM logins WHERE id = ?;";
            PreparedStatement st = con.prepareStatement(sql);
            st.setInt(1, loginId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return populateDTO(rs);
            }

            return null;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public LoginDTO login(String login, String password) {
        String sql;

        if (SchemaThreadLocal.get().equals(Constants.GLOBAL_SCHEMA)) {
            sql =
                    """
                            SELECT id, login, employee, login as name, password, password_salt,
                                    salted_password
                            FROM logins
                            WHERE login = ? and password = ?""";
        } else {
            sql =
                    """
                            SELECT L.id, L.login, L.employee, coalesce(U.name, L.login) as name, password,
                                password_salt, salted_password
                            FROM logins L
                            LEFT JOIN users U ON U.login_id = L.id
                            WHERE L.login = ? and L.password = ?""";
        }

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            PreparedStatementUtil.setAllParameters(pst, login, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return this.populateDTO(rs);
            }

            return null;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean update(LoginDTO login) {
        try (Connection con = datasource.getConnection()) {

            String sql =
                    """
                            UPDATE logins SET employee = ?, modified = now(), modified_by = ?,
                                password_salt = ?, salted_password = ?, password = null WHERE id = ?""";

            PreparedStatement pst = con.prepareStatement(sql);

            PreparedStatementUtil.setAllParameters(
                    pst,
                    login.isEmployee(),
                    login.getModifiedBy(),
                    login.getPasswordSalt(),
                    login.getSaltedPassword(),
                    login.getId());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public LoginDTO getByLogin(String loginName) {
        try (Connection con = datasource.getConnection()) {
            String sqlSelectLogin =
                    "SELECT id, login, employee, password, password_salt, salted_password FROM logins WHERE login = ?;";

            PreparedStatement pstSelectLogin = con.prepareStatement(sqlSelectLogin);
            pstSelectLogin.setString(1, loginName);
            ResultSet rs = pstSelectLogin.executeQuery();

            if (rs != null && rs.next()) {
                return populateDTO(rs);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return null;
    }

    @Override
    public boolean delete(UserDTO userDTO) {
        try (Connection con = datasource.getConnection()) {
            con.setAutoCommit(false);

            String sql;
            PreparedStatement pst;

            sql = "SELECT login_id FROM users WHERE id = ?;";
            pst = con.prepareStatement(sql);
            pst.setInt(1, userDTO.getId());
            ResultSet rs = pst.executeQuery();
            int loginId = 0;
            if (rs != null && rs.next()) {
                loginId = rs.getInt("login_id");
            }

            if (loginId != 0) {
                sql = "UPDATE users SET login_id = null WHERE id = ?;";
                pst = con.prepareStatement(sql);
                pst.setInt(1, userDTO.getId());
                pst.executeUpdate();

                sql = "DELETE FROM logins WHERE id = ?;";
                pst = con.prepareStatement(sql);
                pst.setInt(1, loginId);
                pst.executeUpdate();
            }

            con.commit();
            return true;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public synchronized boolean save(LoginDTO dto, UserDTO udto) {
        return withTransactionContext(
                con -> {
                    int loginId = this.getNextSerial("logins_id_seq");

                    if (loginId != 0) {
                        dto.setId(loginId);
                        String sqlInsertLogin =
                                "INSERT INTO logins (id, login, employee, created_by, password_salt, salted_password) VALUES (?, ?, ?, ?, ?, ?);";
                        PreparedStatement pstInsertLogin = con.prepareStatement(sqlInsertLogin);

                        PreparedStatementUtil.setAllParameters(
                                pstInsertLogin,
                                dto.getId(),
                                dto.getLogin(),
                                dto.isEmployee(),
                                dto.getCreatedBy(),
                                dto.getPasswordSalt(),
                                dto.getSaltedPassword());

                        pstInsertLogin.executeUpdate();

                        String sqlUpdateUser =
                                "UPDATE users SET login_id = ?, modified = now(), modified_by = ? WHERE id = ?;";
                        PreparedStatement pstUpdateUser = con.prepareStatement(sqlUpdateUser);

                        PreparedStatementUtil.setAllParameters(
                                pstUpdateUser, dto.getId(), dto.getCreatedBy(), udto.getId());

                        pstUpdateUser.executeUpdate();

                        udto.setLoginId(dto.getId());
                    }

                    return true;
                });
    }

    @Override
    public LoginDTO login(String login, byte[] saltedPassword) {
        String sql;

        if (SchemaThreadLocal.get().equals(Constants.GLOBAL_SCHEMA)) {
            sql =
                    """
                            SELECT id, login, employee, login as name, password_salt, salted_password,
                                    password
                            FROM logins
                            WHERE login = ? and salted_password = ?""";
        } else {
            sql =
                    """
                            SELECT L.id, L.login, L.employee, coalesce(U.name, L.login) as name,
                                    password_salt, salted_password, password
                            FROM logins L
                            LEFT JOIN users U ON U.login_id = L.id
                            WHERE L.login = ? and L.salted_password = ?""";
        }

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            PreparedStatementUtil.setAllParameters(pst, login, saltedPassword);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return this.populateDTO(rs);
            }

            return null;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private LoginDTO populateDTO(ResultSet rs) throws SQLException {
        LoginDTO dto = new LoginDTO();

        dto.setId(rs.getInt("id"));
        dto.setLogin(rs.getString("login"));
        dto.setEmployee(rs.getBoolean("employee"));
        dto.setPasswordSalt(rs.getBytes("password_salt"));
        dto.setSaltedPassword(rs.getBytes("salted_password"));

        return dto;
    }
}
