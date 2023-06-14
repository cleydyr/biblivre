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
package biblivre.cataloging;

import biblivre.cataloging.authorities.AuthoritiesRecordBO;
import biblivre.cataloging.authorities.AuthorityRecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.dataimport.ImportProcessor;
import biblivre.cataloging.enums.ImportEncoding;
import biblivre.cataloging.vocabulary.VocabularyRecordBO;
import biblivre.cataloging.vocabulary.VocabularyRecordDTO;
import biblivre.core.AbstractBO;
import biblivre.core.function.UnsafeSupplier;
import biblivre.marc.MaterialType;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImportBO extends AbstractBO {
    private BiblioRecordBO biblioRecordBO;
    private VocabularyRecordBO vocabularyRecordBO;
    private AuthoritiesRecordBO authorityRecordBO;

    private Collection<ImportProcessor> importProcessors = Collections.emptyList();

    public ImportDTO loadFromFile(UnsafeSupplier<InputStream> inputStreamSupplier) {
        ImportDTO importDTO = null;

        for (ImportProcessor importProcessor : importProcessors) {
            try (InputStream is = inputStreamSupplier.get()) {
                importDTO = importProcessor.importData(is, this::dtoFromRecord);

                if (importDTO != null && importDTO.isValid()) {
                    break;
                }
            } catch (Exception e) {
                logger.debug("Error reading file", e);
            }
        }

        if (importDTO != null) {
            importDTO.setEncoding(ImportEncoding.AUTO_DETECT);
        }

        return importDTO;
    }

    public RecordDTO dtoFromRecord(Record record) {
        RecordDTO rdto = null;
        RecordBO rbo = null;

        switch (MaterialType.fromRecord(record)) {
            case HOLDINGS:
                break;
            case VOCABULARY:
                rdto = new VocabularyRecordDTO();
                rbo = vocabularyRecordBO;
                break;
            case AUTHORITIES:
                rdto = new AuthorityRecordDTO();
                rbo = authorityRecordBO;
                break;
            default:
                rdto = new BiblioRecordDTO();
                rbo = biblioRecordBO;
                break;
        }

        if (rdto != null && rbo != null) {
            rdto.setRecord(record);
            rdto.setId(0);
            rbo.populateDetails(rdto, RecordBO.MARC_INFO);
        }

        return rdto;
    }

    protected static final Logger logger = LoggerFactory.getLogger(ImportBO.class);

    @Autowired
    public void setBiblioRecordBO(BiblioRecordBO biblioRecordBO) {
        this.biblioRecordBO = biblioRecordBO;
    }

    @Autowired
    public void setVocabularyRecordBO(VocabularyRecordBO vocabularyRecordBO) {
        this.vocabularyRecordBO = vocabularyRecordBO;
    }

    @Autowired
    public void setAuthorityRecordBO(AuthoritiesRecordBO authorityRecordBO) {
        this.authorityRecordBO = authorityRecordBO;
    }

    @Autowired
    public void setImportProcessors(Collection<ImportProcessor> importProcessors) {
        this.importProcessors = importProcessors;
    }
}
