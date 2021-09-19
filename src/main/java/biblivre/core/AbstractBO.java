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
package biblivre.core;

import biblivre.core.auth.AuthorizationBO;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.utils.Constants;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBO {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String schema;

    private static Map<Class<? extends AbstractBO>, AbstractBO> instances = new HashMap<>();

    @SuppressWarnings("unchecked")
    protected static <T extends AbstractBO> T getInstance(Class<T> cls) {
        T instance = (T) AbstractBO.instances.get(cls);

        if (instance == null) {
            if (!AbstractBO.class.isAssignableFrom(cls)) {
                throw new IllegalArgumentException(
                        "BO: getInstance: Class " + cls.getName() + " is not a subclass of BO.");
            }

            try {
                instance = cls.newInstance();

                AbstractBO.instances.put(cls, instance);
            } catch (Exception ex) {
            }
        }

        return instance;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSchema() {
        return StringUtils.defaultString(this.schema, Constants.GLOBAL_SCHEMA);
    }

    public boolean isGlobalSchema() {
        return this.getSchema().equals(Constants.GLOBAL_SCHEMA);
    }

    public void authorize(String module, String action, AuthorizationPoints authorizationPoints) {
        if (authorizationPoints == null) {
            authorizationPoints = AuthorizationPoints.getNotLoggedInstance(schema);
        }

        AuthorizationBO abo = AuthorizationBO.getInstance();

        abo.authorize(authorizationPoints, module, action);
    }
}
