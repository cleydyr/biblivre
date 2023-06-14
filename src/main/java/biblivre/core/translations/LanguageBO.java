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

import biblivre.core.SchemaThreadLocal;
import biblivre.core.utils.StringPool;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LanguageBO {
    private static final Logger logger = LoggerFactory.getLogger(LanguageBO.class);

    private LanguageDAO languageDAO;

    public Set<LanguageDTO> getLanguages() {
        return loadLanguages(SchemaThreadLocal.get());
    }

    public boolean isLoaded(String language) {
        if (StringUtils.isBlank(language)) {
            return false;
        }

        Set<LanguageDTO> languages = getLanguages();

        if (languages == null) {
            return false;
        }

        for (LanguageDTO dto : languages) {
            if (language.equals(dto.getLanguage())) {
                return true;
            }
        }

        return false;
    }

    public boolean isNotLoaded(String language) {
        return !isLoaded(language);
    }

    public String getDefaultLanguage() {
        return getLanguages().stream()
                .findFirst()
                .orElse(
                        new LanguageDTO() {
                            @Override
                            public String getLanguage() {
                                return StringPool.BLANK;
                            }
                        })
                .getLanguage();
    }

    public LanguageDTO getLanguage(String language) {
        Set<LanguageDTO> languages = getLanguages();

        for (LanguageDTO dto : languages) {
            if (language.equals(dto.getLanguage())) {
                return dto;
            }
        }

        return null;
    }

    private Set<LanguageDTO> loadLanguages(String schema) {
        LanguageBO.logger.debug("Loading languages from " + schema);

        return languageDAO.list();
    }

    @Autowired
    public void setLanguageDAO(LanguageDAO languageDAO) {
        this.languageDAO = languageDAO;
    }
}
