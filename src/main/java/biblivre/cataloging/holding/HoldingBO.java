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
package biblivre.cataloging.holding;

import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.HoldingAvailability;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.labels.LabelDTO;
import biblivre.cataloging.labels.LabelGenerator;
import biblivre.cataloging.search.SearchDTO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.DTOCollection;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.DiskFile;
import biblivre.core.utils.Constants;
import biblivre.core.utils.ParagraphAlignmentUtil;
import biblivre.labels.print.LabelPrintDTO;
import biblivre.login.LoginBO;
import biblivre.login.LoginDTO;
import biblivre.marc.MarcConstants;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import com.lowagie.text.Element;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HoldingBO extends RecordBO {
    private HoldingDAO holdingDAO;
    private UserBO userBO;
    private LoginBO loginBO;
    private ConfigurationBO configurationBO;
    private LabelGenerator labelGenerator;

    public Map<Integer, RecordDTO> map(Set<Integer> ids) {
        return this.holdingDAO.map(ids);
    }

    public Map<Integer, RecordDTO> map(Set<Integer> ids, int mask) {
        Map<Integer, RecordDTO> map = this.holdingDAO.map(ids);

        for (RecordDTO dto : map.values()) {
            this.populateDetails(dto, mask);
        }

        return map;
    }

    public List<RecordDTO> list(int offset, int limit) {
        return this.holdingDAO.list(offset, limit);
    }

    public Integer count() {
        return this.count(0);
    }

    public Integer count(int recordId) {
        return this.holdingDAO.count(recordId, false);
    }

    public Integer countAvailableHoldings(int recordId) {
        return this.holdingDAO.count(recordId, true);
    }

    public void markAsPrinted(Set<Integer> ids) {
        this.holdingDAO.markAsPrinted(ids);
    }

    public String getNextAccessionNumber() {
        String prefix = configurationBO.getString(Constants.CONFIG_ACCESSION_NUMBER_PREFIX);
        int year = Calendar.getInstance().get(Calendar.YEAR);

        String accessionPrefix = prefix + "." + year + ".";
        return accessionPrefix + this.holdingDAO.getNextAccessionNumber(accessionPrefix);
    }

    public boolean isAccessionNumberAvailable(String accessionNumber, int holdingSerial) {
        return this.holdingDAO.isAccessionNumberAvailable(accessionNumber, holdingSerial);
    }

    public boolean isAccessionNumberAvailable(String accessionNumber) {
        return this.isAccessionNumberAvailable(accessionNumber, 0);
    }

    public DTOCollection<HoldingDTO> list(int recordId) {
        return this.holdingDAO.list(recordId);
    }

    public HoldingDTO getByAccessionNumber(String accessionNumber) {
        return this.holdingDAO.getByAccessionNumber(accessionNumber);
    }

    public DTOCollection<HoldingDTO> search(
            String query, RecordDatabase database, boolean lentOnly, int offset, int limit) {
        DTOCollection<HoldingDTO> searchResults =
                this.holdingDAO.search(query, database, lentOnly, offset, limit);
        for (HoldingDTO holding : searchResults) {
            MarcDataReader reader = new MarcDataReader(holding.getRecord());
            holding.setShelfLocation(reader.getShelfLocation());
        }
        return searchResults;
    }

    public RecordDTO get(int id) {
        Set<Integer> ids = new HashSet<>();

        ids.add(id);

        return this.map(ids).get(id);
    }

    public RecordDTO get(int id, int mask) {
        Set<Integer> ids = new HashSet<>();
        ids.add(id);

        return this.map(ids, mask).get(id);
    }

    public boolean save(RecordDTO dto) {
        HoldingDTO hdto = (HoldingDTO) dto;
        Record record = hdto.getRecord();
        MarcDataReader marcReader = new MarcDataReader(record);

        String accessionNumber = marcReader.getAccessionNumber();
        String holdingLocation = marcReader.getHoldingLocation();

        if (StringUtils.isBlank(accessionNumber)) {
            accessionNumber = this.getNextAccessionNumber();
            MarcUtils.setAccessionNumber(record, accessionNumber);

        } else if (!this.isAccessionNumberAvailable(accessionNumber)) {
            throw new ValidationException("cataloging.holding.error.accession_number_unavailable");
        }

        hdto.setDateOfLastTransaction();

        // Availability and RecordId are already populated at this point.
        hdto.setAccessionNumber(accessionNumber);
        hdto.setLocationD(holdingLocation);

        boolean success = this.holdingDAO.save(dto);

        if (success) {
            // UPDATE holding_creation_counter
            try {
                UserDTO udto = userBO.getUserByLoginId(dto.getCreatedBy());
                LoginDTO login = loginBO.get(dto.getCreatedBy());
                this.holdingDAO.updateHoldingCreationCounter(udto, login);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return success;
    }

    public boolean update(RecordDTO dto) {
        HoldingDTO hdto = (HoldingDTO) dto;
        Record record = hdto.getRecord();
        MarcDataReader marcReader = new MarcDataReader(record);

        String accessionNumber = marcReader.getAccessionNumber();
        String holdingLocation = marcReader.getHoldingLocation();

        if (StringUtils.isBlank(accessionNumber)) {
            accessionNumber = this.getNextAccessionNumber();
            MarcUtils.setAccessionNumber(record, accessionNumber);

        } else if (!this.isAccessionNumberAvailable(accessionNumber, hdto.getId())) {
            throw new ValidationException("cataloging.holding.error.accession_number_unavailable");
        }

        dto.setDateOfLastTransaction();

        // Id, Availability and RecordId are already populated at this point.
        hdto.setAccessionNumber(accessionNumber);
        hdto.setLocationD(holdingLocation);

        return this.holdingDAO.update(dto);
    }

    public boolean delete(RecordDTO dto) {
        return this.holdingDAO.delete(dto);
    }

    public void populateDetails(RecordDTO rdto, int mask) {

        if ((mask & RecordBO.MARC_INFO) != 0) {
            MarcDataReader reader = new MarcDataReader(rdto.getRecord());
            ((HoldingDTO) rdto).setShelfLocation(reader.getShelfLocation());
        }
    }

    public boolean paginateHoldingSearch(SearchDTO search) {
        Map<Integer, Integer> groupCount = this.holdingDAO.countSearchResults(search);
        Integer count = groupCount.get(search.getIndexingGroup());

        if (count == null || count == 0) {
            return false;
        }

        List<RecordDTO> list = this.holdingDAO.getSearchResults(search);

        search.getPaging().setRecordCount(count);
        search.setIndexingGroupCount(groupCount);

        for (RecordDTO rdto : list) {
            this.populateDetails(rdto, RecordBO.MARC_INFO | RecordBO.HOLDING_INFO);
            search.add(rdto);
        }

        return true;
    }

    public DiskFile printLabelsToPDF(List<LabelDTO> labels, LabelPrintDTO printDTO) {
        int horizontalAlignment =
                ParagraphAlignmentUtil.getHorizontalAlignmentConfigurationValue(
                        () -> Element.ALIGN_CENTER,
                        configurationBO.getString(
                                Constants.CONFIG_LABEL_PRINT_PARAGRAPH_ALIGNMENT));

        return labelGenerator.generate(labels, printDTO, horizontalAlignment);
    }

    public boolean createAutomaticHolding(AutomaticHoldingDTO autoDto) {

        int holdingCount = autoDto.getHoldingCount();
        int volumeNumber = autoDto.getIssueNumber();
        int volumeCount = autoDto.getNumberOfIssues();
        String library = autoDto.getLibraryName();
        String acquisitionType = autoDto.getAcquisitionType();
        String acquisitionDate = autoDto.getAcquisitionDate();

        String[] notes = new String[3];
        notes[0] = 'a' + library;
        notes[1] = 'c' + acquisitionType;
        notes[2] = 'd' + acquisitionDate;

        boolean success = true;

        RecordDTO biblioDto = autoDto.getBiblioRecordDto();

        MarcDataReader mdr = new MarcDataReader(biblioDto.getRecord());

        String biblioLocationA = mdr.getLocation();
        biblioLocationA = StringUtils.defaultString(biblioLocationA);
        String biblioLocationB = mdr.getLocationB();
        biblioLocationB = StringUtils.defaultString(biblioLocationB);
        String biblioLocationC = mdr.getLocationC();
        biblioLocationC = StringUtils.defaultString(biblioLocationC);

        for (int j = 0; j < volumeCount; j++) {
            for (int i = 0; i < holdingCount; i++) {

                String[] location = new String[4];

                location[0] = "a" + biblioLocationA;
                location[1] = "b" + biblioLocationB;
                location[2] = "c";

                if (StringUtils.isNotBlank(biblioLocationC)) {
                    location[2] += biblioLocationC;
                } else {
                    if (volumeNumber != 0) {
                        location[2] += "v." + volumeNumber;
                    } else if (volumeCount > 1) {
                        location[2] += "v." + (j + 1);
                    }
                }

                location[3] = 'd' + "ex." + (i + 1);

                Record holding = this.createHoldingMarcRecord(location, notes);
                MarcUtils.setAccessionNumber(holding, this.getNextAccessionNumber());

                success &= this.save(this.createHoldingDto(holding, autoDto));
            }
        }

        return success;
    }

    private Record createHoldingMarcRecord(String[] location, String[] notes) {
        MarcFactory factory = MarcFactory.newInstance();
        Record record = factory.newRecord();

        DataField df =
                factory.newDataField(
                        MarcConstants.SHELF_LOCATION,
                        MarcConstants.NO_INDICATOR,
                        MarcConstants.NO_INDICATOR);
        record.addVariableField(df);
        for (int i = 0; i < 4; i++) {
            if (StringUtils.isNotBlank(location[i])) {
                final char code = location[i].charAt(0);
                Subfield subfield = factory.newSubfield(code, location[i].substring(1));
                df.addSubfield(subfield);
            }
        }

        DataField df1 =
                factory.newDataField(
                        MarcConstants.SOURCE_ACQUISITION_NOTES,
                        MarcConstants.NO_INDICATOR,
                        MarcConstants.NO_INDICATOR);
        record.addVariableField(df1);
        for (int i = 0; i < 3; i++) {
            if (StringUtils.isNotBlank(notes[i])) {
                final char code = notes[i].charAt(0);
                Subfield subfield = factory.newSubfield(code, notes[i].substring(1));
                df1.addSubfield(subfield);
            }
        }

        record.setLeader(MarcUtils.createBasicLeader(MaterialType.HOLDINGS, RecordStatus.NEW));

        return record;
    }

    private HoldingDTO createHoldingDto(Record record, AutomaticHoldingDTO autoDto) {
        HoldingDTO dto = new HoldingDTO();

        dto.setRecord(record);
        dto.setAvailability(HoldingAvailability.AVAILABLE);
        dto.setRecordDatabase(autoDto.getDatabase());
        dto.setRecordId(autoDto.getBiblioRecordDto().getId());
        dto.setCreatedBy(autoDto.getCreatedBy());

        return dto;
    }

    public RecordType getRecordType() {
        return RecordType.HOLDING;
    }

    protected static final Logger logger = LoggerFactory.getLogger(HoldingBO.class);

    @Autowired
    public void setHoldingDAO(HoldingDAO holdingDAO) {
        this.holdingDAO = holdingDAO;
    }

    @Autowired
    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }

    @Autowired
    public void setLoginBO(LoginBO loginBO) {
        this.loginBO = loginBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }

    @Autowired
    public void setLabelGenerator(LabelGenerator labelGenerator) {
        this.labelGenerator = labelGenerator;
    }
}
