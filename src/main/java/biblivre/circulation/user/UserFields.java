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
package biblivre.circulation.user;

import biblivre.core.JavascriptCacheableList;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.StaticBO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFields extends StaticBO {

    private static Logger logger = LoggerFactory.getLogger(UserFields.class);

    private static HashMap<String, JavascriptCacheableList<UserFieldDTO>> fields; // FormTab

    private UserFields() {}

    static {
        UserFields.resetAll();
    }

    public static void resetAll() {
        UserFields.fields = new HashMap<>();
    }

    public static void reset() {
        UserFields.fields.remove(SchemaThreadLocal.get());
    }

    public static JavascriptCacheableList<UserFieldDTO> getFields() {
        JavascriptCacheableList<UserFieldDTO> list = _getScoped();

        if (list == null) {
            list = UserFields.loadFields();
        }

        return list;
    }

    public static List<UserFieldDTO> getSearchableFields() {
        JavascriptCacheableList<UserFieldDTO> list = getFields();

        List<UserFieldDTO> searcheableList = new ArrayList<>();
        for (UserFieldDTO dto : list) {

            switch (dto.getType()) {
                case STRING:
                case TEXT:
                case NUMBER:
                    searcheableList.add(dto);
                    break;
                case DATE:
                case DATETIME:
                case BOOLEAN:
                case LIST:
                default:
                    break;
            }
        }

        return searcheableList;
    }

    private static synchronized JavascriptCacheableList<UserFieldDTO> loadFields() {
        JavascriptCacheableList<UserFieldDTO> list = _getScoped();

        // Checking again for thread safety.
        if (list != null) {
            return list;
        }

        if (UserFields.logger.isDebugEnabled()) {
            UserFields.logger.debug("Loading user fields from " + SchemaThreadLocal.get() + ".");
        }

        UserFieldsDAO dao = UserFieldsDAOImpl.getInstance();

        List<UserFieldDTO> fields = dao.listFields();
        list =
                new JavascriptCacheableList<>(
                        "CirculationInput.userFields",
                        SchemaThreadLocal.get() + ".circulation",
                        ".user_fields.js");
        list.addAll(fields);

        _putScoped(list);

        return list;
    }

    private static void _putScoped(JavascriptCacheableList<UserFieldDTO> list) {
        UserFields.fields.put(SchemaThreadLocal.get(), list);
    }

    private static JavascriptCacheableList<UserFieldDTO> _getScoped() {
        JavascriptCacheableList<UserFieldDTO> list = UserFields.fields.get(SchemaThreadLocal.get());
        return list;
    }
}
