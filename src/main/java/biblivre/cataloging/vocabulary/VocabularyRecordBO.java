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
package biblivre.cataloging.vocabulary;

import biblivre.administration.indexing.IndexingDAO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.PaginableRecordBO;
import biblivre.cataloging.enums.RecordType;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import java.util.Map;
import java.util.Set;
import org.marc4j.marc.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("vocabulary")
public class VocabularyRecordBO extends PaginableRecordBO {
    private IndexingDAO indexingDAO;

    @Override
    public void populateDetails(RecordDTO rdto, int mask) {
        if (rdto == null) {
            return;
        }

        VocabularyRecordDTO dto = (VocabularyRecordDTO) rdto;

        if ((mask & RecordBO.MARC_INFO) != 0) {
            Record record = rdto.getRecord();

            if (record == null && rdto.getIso2709() != null) {
                record = MarcUtils.iso2709ToRecord(rdto.getIso2709());
            }

            if (record != null) {
                MarcDataReader marcDataReader = new MarcDataReader(record);
                dto.setTermTE(marcDataReader.getFirstSubfieldData("150", 'a'));
                dto.setTermUP(marcDataReader.getFirstSubfieldData("450", 'a'));
                dto.setTermTG(marcDataReader.getFirstSubfieldData("550", 'a'));
                dto.setTermVTTA(marcDataReader.getFirstSubfieldData("360", 'a'));
            }
        }
    }

    @Override
    public boolean save(RecordDTO dto) {
        dto.setDateOfLastTransaction();
        dto.setFixedLengthDataElements();

        if (this.recordDAO.save(dto)) {
            indexingDAO.reindex(
                    dto,
                    indexingGroupBO.getGroups(dto.getRecordType()),
                    tabFieldsBO.getAutocompleteSubFields(dto.getRecordType()));
            return true;
        }

        return false;
    }

    @Override
    public boolean update(RecordDTO dto) {
        dto.setDateOfLastTransaction();

        if (this.recordDAO.update(dto)) {
            indexingDAO.reindex(
                    dto,
                    indexingGroupBO.getGroups(dto.getRecordType()),
                    tabFieldsBO.getAutocompleteSubFields(dto.getRecordType()));
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(RecordDTO dto) {
        if (this.recordDAO.delete(dto)) {
            indexingDAO.deleteIndexes(RecordType.VOCABULARY, dto);
        }

        return true;
    }

    @Override
    public Map<Integer, RecordDTO> map(Set<Integer> ids) {
        return super.map(ids, RecordBO.MARC_INFO);
    }

    @Override
    public RecordType getRecordType() {
        return RecordType.VOCABULARY;
    }

    @Autowired
    public void setIndexingDAO(IndexingDAO indexingDAO) {
        this.indexingDAO = indexingDAO;
    }
}
