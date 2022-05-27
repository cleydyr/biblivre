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
package biblivre.cataloging.bibliographic;

import biblivre.administration.indexing.IndexingDAO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.lending.LendingDAO;
import biblivre.circulation.reservation.ReservationDAO;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BiblioRecordBO extends PaginableRecordBO {

    private IndexingDAO indexingDAO;
    private ReservationDAO reservationDAO;
    private LendingDAO lendingDAO;

    @Override
    public void populateDetails(RecordDTO recordDTO, int mask) {
        if (recordDTO == null) {
            return;
        }

        if ((mask & RecordBO.MARC_INFO) != 0) {
            populateMarcInfo(recordDTO);
        }

        BiblioRecordDTO biblioRecordDTO = (BiblioRecordDTO) recordDTO;

        Integer recordId = biblioRecordDTO.getId();

        if (recordId == null || recordId <= 0) {
            return;
        }

        if ((mask & RecordBO.HOLDING_INFO) != 0) {
            int totalHoldings = holdingBO.count(recordId);
            int availableHoldings = holdingBO.countAvailableHoldings(recordId);
            int lentCount = 0;
            int reservedCount = 0;

            if (availableHoldings > 0) {
                lentCount = lendingDAO.countLentHoldings(recordId);
                reservedCount = reservationDAO.countByRecord(biblioRecordDTO);
            }

            biblioRecordDTO.setHoldingsCount(totalHoldings);
            biblioRecordDTO.setHoldingsAvailable(availableHoldings - lentCount);
            biblioRecordDTO.setHoldingsLent(lentCount);
            biblioRecordDTO.setHoldingsReserved(reservedCount);
        }

        if ((mask & RecordBO.HOLDING_LIST) != 0) {
            List<HoldingDTO> holdingsList = holdingBO.list(recordId);

            Collections.sort(holdingsList);

            for (HoldingDTO holding : holdingsList) {
                MarcDataReader marcDataReader = new MarcDataReader(holding.getRecord());

                String holdingLocation = marcDataReader.getShelfLocation();
                holding.setShelfLocation(
                        StringUtils.isNotBlank(holdingLocation)
                                ? holdingLocation
                                : biblioRecordDTO.getShelfLocation());
            }

            biblioRecordDTO.setHoldings(holdingsList);
        }
    }

    public void populateMarcInfo(RecordDTO recordDTO) {
        BiblioRecordDTO biblioRecordDTO = (BiblioRecordDTO) recordDTO;

        Record record = recordDTO.getRecord();

        if (record == null && recordDTO.getIso2709() != null) {
            record = MarcUtils.iso2709ToRecord(recordDTO.getIso2709());
        }

        if (record != null) {
            MarcDataReader marcDataReader = new MarcDataReader(record);

            biblioRecordDTO.setAuthor(marcDataReader.getAuthor(true));
            biblioRecordDTO.setTitle(marcDataReader.getTitle(false));
            biblioRecordDTO.setIsbn(marcDataReader.getIsbn());
            biblioRecordDTO.setIssn(marcDataReader.getIssn());
            biblioRecordDTO.setIsrc(marcDataReader.getIsrc());
            biblioRecordDTO.setPublicationYear(marcDataReader.getPublicationYear());
            biblioRecordDTO.setShelfLocation(marcDataReader.getShelfLocation());
            biblioRecordDTO.setSubject(marcDataReader.getSubject(true));
        }
    }

    @Override
    public boolean save(RecordDTO dto) {
        dto.setDateOfLastTransaction();
        dto.setFixedLengthDataElements();

        if (this.recordDAO.save(dto)) {
            indexingDAO.reindex(dto);
            return true;
        }

        return false;
    }

    @Override
    public boolean update(RecordDTO dto) {
        dto.setDateOfLastTransaction();

        if (this.recordDAO.update(dto)) {
            indexingDAO.reindex(dto);
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

        if (this.recordDAO.delete(dto)) {
            indexingDAO.deleteIndexes(RecordType.BIBLIO, dto);
            //			HoldingBO hbo = new HoldingBO();
            //			hbo.delete(dto);
        }
        return true;
    }

    @Override
    public Map<Integer, RecordDTO> map(Set<Integer> ids) {
        return super.map(ids, RecordBO.MARC_INFO | RecordBO.HOLDING_INFO);
    }

    @Override
    public RecordType getRecordType() {
        return RecordType.BIBLIO;
    }

    @Autowired
    public void setIndexingDAO(IndexingDAO indexingDAO) {
        this.indexingDAO = indexingDAO;
    }

    @Autowired
    public void setLendingDAO(LendingDAO lendingDAO) {
        this.lendingDAO = lendingDAO;
    }

    @Autowired
    public void setReservationDAO(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    public List<RecordDTO> list(int offset, int limit, RecordDatabase database) {
        return recordDAO.list(offset, limit, database, getRecordType());
    }
}
