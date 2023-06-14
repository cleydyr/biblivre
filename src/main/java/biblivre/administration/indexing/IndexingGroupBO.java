/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser útil,
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
package biblivre.administration.indexing;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.translations.TranslationBO;
import biblivre.core.translations.TranslationsMap;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndexingGroupBO {
    private static final Logger logger = LoggerFactory.getLogger(IndexingGroupBO.class);

    private IndexingGroupsDAO indexingGroupDAO;

    private TranslationBO translationBO;

    public List<IndexingGroupDTO> getGroups(RecordType recordType) {
        return loadGroups(SchemaThreadLocal.get(), recordType);
    }

    public String getSearchableGroupsText(RecordType recordType, String language) {
        List<String> list = new ArrayList<>();

        TranslationsMap translations = translationBO.get(language);

        for (IndexingGroupDTO group : getGroups(recordType)) {
            if (group.getId() == 0) {
                continue;
            }

            list.add(
                    translations.getText(
                            "cataloging."
                                    + (recordType == RecordType.BIBLIO
                                            ? "bibliographic"
                                            : recordType)
                                    + ".indexing_groups."
                                    + group.getTranslationKey()));
        }

        return StringUtils.join(list, ", ");
    }

    public Integer getDefaultSortableGroupId(RecordType recordType) {
        IndexingGroupDTO sort = null;

        for (IndexingGroupDTO group : getGroups(recordType)) {
            if (group.isSortable()) {
                if (group.isDefaultSort()) {
                    sort = group;
                    break;
                }

                if (sort == null) {
                    sort = group;
                }
            }
        }

        return (sort != null) ? sort.getId() : 1;
    }

    private List<IndexingGroupDTO> loadGroups(String schema, RecordType recordType) {

        if (IndexingGroupBO.logger.isDebugEnabled()) {
            IndexingGroupBO.logger.debug(
                    "Loading indexing groups from " + schema + "." + recordType);
        }

        return indexingGroupDAO.list(recordType);
    }

    @Autowired
    public void setIndexingGroupDAO(IndexingGroupsDAO indexingGroupDAO) {
        this.indexingGroupDAO = indexingGroupDAO;
    }

    @Autowired
    public void setTranslationsBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }
}
