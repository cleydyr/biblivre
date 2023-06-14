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
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFieldBO {

    private static final Logger logger = LoggerFactory.getLogger(UserFieldBO.class);

    private final UserFieldsDAO userFieldsDAO;

    @Autowired
    public UserFieldBO(UserFieldsDAO userFieldsDAO) {
        this.userFieldsDAO = userFieldsDAO;
    }

    public JavascriptCacheableList<UserFieldDTO> getFields() {
        return loadFields();
    }

    public List<UserFieldDTO> getSearchableFields() {
        return getFields().stream()
                .filter(userField -> userField.getType().isSearchable())
                .toList();
    }

    private JavascriptCacheableList<UserFieldDTO> loadFields() {
        if (UserFieldBO.logger.isDebugEnabled()) {
            UserFieldBO.logger.debug("Loading user fields from " + SchemaThreadLocal.get() + ".");
        }

        List<UserFieldDTO> fields = userFieldsDAO.listFields();

        JavascriptCacheableList<UserFieldDTO> list =
                new JavascriptCacheableList<>(
                        "CirculationInput.userFields",
                        SchemaThreadLocal.get() + ".circulation",
                        ".user_fields.js");

        list.addAll(fields);

        return list;
    }
}
