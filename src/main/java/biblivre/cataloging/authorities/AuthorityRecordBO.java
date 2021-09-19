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
package biblivre.cataloging.authorities;

import biblivre.administration.indexing.IndexingBO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.core.exceptions.ValidationException;
import java.util.Map;
import java.util.Set;

public class AuthorityRecordBO extends RecordBO {

    @Override
    public void populateDetails(RecordDTO rdto, int mask) {}

    @Override
    public boolean save(RecordDTO dto) {
        dto.setDateOfLastTransaction();
        dto.setFixedLengthDataElements();

        if (this.rdao.save(dto)) {
            IndexingBO indexingBo = IndexingBO.getInstance(this.getSchema());
            indexingBo.reindex(RecordType.AUTHORITIES, dto);
            return true;
        }

        return false;
    }

    @Override
    public boolean update(RecordDTO dto) {
        dto.setDateOfLastTransaction();

        if (this.rdao.update(dto)) {
            IndexingBO indexingBo = IndexingBO.getInstance(this.getSchema());
            indexingBo.reindex(RecordType.AUTHORITIES, dto);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(RecordDTO dto) {

        //		HoldingBO holdingBo = new HoldingBO();
        //		LendingBO lendingBo = new LendingBO();
        //		List<HoldingDTO> holdings = holdingBo.list(record);
        //		for (HoldingDTO holding : holdings) {
        //			if (lendingBo.isLent(holding) || lendingBo.wasLent(holding)) {
        //				throw new RuntimeException("MESSAGE_DELETE_BIBLIO_ERROR");
        //			}
        //		}

        if (this.rdao.delete(dto)) {
            IndexingBO indexingBo = IndexingBO.getInstance(this.getSchema());
            indexingBo.deleteIndexes(RecordType.AUTHORITIES, dto);
            //			HoldingBO hbo = new HoldingBO();
            //			hbo.delete(dto);
        }
        return true;
    }

    @Override
    public boolean isDeleatable(HoldingDTO holding) throws ValidationException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<Integer, RecordDTO> map(Set<Integer> ids) {
        return super.map(ids, RecordBO.MARC_INFO);
    }

    @Override
    public RecordType getRecordType() {
        return RecordType.AUTHORITIES;
    }
}
