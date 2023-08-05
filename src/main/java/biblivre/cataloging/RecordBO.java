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

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.core.AbstractBO;
import biblivre.core.DTOCollection;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.file.DiskFile;
import biblivre.core.utils.TextUtils;
import biblivre.digitalmedia.DigitalMediaBO;
import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class RecordBO extends AbstractBO {

    protected RecordDAO recordDAO;
    private DigitalMediaBO digitalMediaBO;
    protected ConfigurationBO configurationBO;

    public static final int FULL = 1;
    public static final int MARC_INFO = 1 << 1;
    public static final int HOLDING_INFO = 1 << 2;
    public static final int HOLDING_LIST = 1 << 3;
    public static final int ATTACHMENTS_LIST = 1 << 6;

    public static final Pattern ID_PATTERN = Pattern.compile("id=(.*?)(&|$)");

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

    public Map<Integer, RecordDTO> map(Set<Integer> ids) {
        return this.recordDAO.map(ids, getRecordType());
    }

    public Map<Integer, RecordDTO> map(Set<Integer> ids, int mask) {
        Map<Integer, RecordDTO> map = this.recordDAO.map(ids, getRecordType());

        for (RecordDTO dto : map.values()) {
            this.populateDetails(dto, mask);
        }

        return map;
    }

    public List<RecordDTO> list(int offset, int limit) {
        return this.recordDAO.list(offset, limit, getRecordType());
    }

    public List<RecordDTO> listByLetter(char letter, int order) {
        return this.populateDetails(
                this.recordDAO.listByLetter(letter, order, getRecordType()), RecordBO.MARC_INFO);
    }

    public boolean save(RecordDTO dto) {
        return this.recordDAO.save(dto);
    }

    public boolean update(RecordDTO dto) {
        return this.recordDAO.update(dto);
    }

    public boolean moveRecords(
            Set<Integer> ids,
            RecordDatabase recordDatabase,
            int modifiedBy,
            AuthorizationPoints authorizationPoints) {
        if (recordDatabase == RecordDatabase.PRIVATE || listContainsPrivateRecord(ids)) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        return this.recordDAO.moveRecords(ids, modifiedBy, recordDatabase, getRecordType());
    }

    public boolean listContainsPrivateRecord(Set<Integer> ids) {
        return this.recordDAO.listContainsPrivateRecord(ids, getRecordType());
    }

    public DiskFile createExportFile(Set<Integer> ids, AuthorizationPoints authorizationPoints) {
        if (listContainsPrivateRecord(ids)) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        Map<Integer, RecordDTO> records = this.map(ids);

        try {
            File marcFile = File.createTempFile("biblivre", ".mrc");

            try (OutputStream out = Files.newOutputStream(marcFile.toPath())) {
                MarcStreamWriter writer = new MarcStreamWriter(out, StandardCharsets.UTF_8.name());

                for (RecordDTO dto : records.values()) {
                    writer.write(dto.getRecord());
                }

                writer.close();

                return new DiskFile(marcFile, "x-download");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public boolean delete(RecordDTO dto) {
        return this.recordDAO.delete(dto);
    }

    public Integer count() {
        SearchDTO search = new SearchDTO(getRecordType());

        return this.count(search);
    }

    private Integer count(SearchDTO search) {
        return this.recordDAO.count(search);
    }

    public List<RecordDTO> populateDetails(List<RecordDTO> list, int mask) {
        for (RecordDTO rdto : list) {
            this.populateDetails(rdto, mask);
        }

        return list;
    }

    public List<String> phraseAutocomplete(String datafield, String subfield, String query) {
        String[] searchTerms = TextUtils.prepareAutocomplete(query);

        List<String> listA =
                this.recordDAO.phraseAutocomplete(
                        datafield, subfield, searchTerms, 10, true, getRecordType());
        List<String> listB =
                this.recordDAO.phraseAutocomplete(
                        datafield, subfield, searchTerms, 5, false, getRecordType());

        listA.addAll(listB);

        return listA;
    }

    public DTOCollection<AutocompleteDTO> recordAutocomplete(
            String datafield, String subfield, String query) {
        String[] searchTerms = TextUtils.prepareAutocomplete(query);

        DTOCollection<AutocompleteDTO> listA =
                this.recordDAO.recordAutocomplete(
                        datafield, subfield, searchTerms, 10, true, getRecordType());
        DTOCollection<AutocompleteDTO> listB =
                this.recordDAO.recordAutocomplete(
                        datafield, subfield, searchTerms, 5, false, getRecordType());

        listA.addAll(listB);

        return listA;
    }

    public void addAttachment(int recordId, String uri, String description, Integer userId) {
        RecordDTO recordDTO = get(recordId);

        recordDTO.addAttachment(uri, description);

        recordDTO.setModifiedBy(userId);

        update(recordDTO);
    }

    public void removeAttachment(Integer recordId, String uri, String description, Integer userId) {
        RecordDTO dto = this.get(recordId);

        dto.removeAttachment(uri, description);

        dto.setModifiedBy(userId);

        this.update(dto);

        // Check if the file is in Biblivre's DB and try to delete it
        Matcher matcher = RecordBO.ID_PATTERN.matcher(uri);
        if (matcher.find()) {
            String encodedId = matcher.group(1);
            String fileId = "";
            String fileName = "";
            String decodedId =
                    new String(Base64.getDecoder().decode(encodedId), StandardCharsets.UTF_8);
            String[] splitId = decodedId.split(":");
            if (splitId.length == 2 && StringUtils.isNumeric(splitId[0])) {
                fileId = splitId[0];
                fileName = splitId[1];
            }

            // Try to remove the file from Biblivre DB
            digitalMediaBO.delete(Integer.valueOf(fileId), fileName);
        }
    }

    public abstract void populateDetails(RecordDTO record, int mask);

    public abstract RecordType getRecordType();

    public RecordDTO open(int id, AuthorizationPoints authorizationPoints) {
        RecordDTO dto = get(id);

        if (dto == null) {
            return null;
        }

        if (dto.getRecordDatabase() == RecordDatabase.PRIVATE) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        populateDetails(dto, RecordBO.MARC_INFO | RecordBO.HOLDING_INFO | RecordBO.HOLDING_LIST);
        return dto;
    }

    public boolean saveOrUpdate(
            RecordDTO recordDTO, int loggedUserId, AuthorizationPoints authorizationPoints) {
        if (recordDTO.getRecordDatabase() == RecordDatabase.PRIVATE) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        boolean success;

        if (recordDTO.isNew()) {
            recordDTO.setCreatedBy(loggedUserId);

            success = save(recordDTO);
        } else {
            recordDTO.setModifiedBy(loggedUserId);

            success = update(recordDTO);
        }

        return success;
    }

    public boolean delete(
            RecordDTO dto, int loggedUserId, AuthorizationPoints authorizationPoints) {
        RecordDatabase recordDatabase = dto.getRecordDatabase();

        if (recordDatabase == RecordDatabase.PRIVATE) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        dto.setModifiedBy(loggedUserId);

        boolean success;

        if (recordDatabase == RecordDatabase.TRASH) {
            success = this.recordDAO.delete(dto);
        } else {
            Set<Integer> ids = new HashSet<>();

            ids.add(dto.getId());

            success =
                    this.recordDAO.moveRecords(
                            ids, loggedUserId, RecordDatabase.TRASH, getRecordType());
        }
        return success;
    }

    public int count(SearchDTO searchDTO, AuthorizationPoints authorizationPoints) {
        if (searchDTO.getQuery().getDatabase() == RecordDatabase.PRIVATE) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        return count(searchDTO);
    }

    protected static final Logger logger = LoggerFactory.getLogger(RecordBO.class);

    @Autowired
    public final void setRecordDAO(RecordDAO recordDAO) {
        this.recordDAO = recordDAO;
    }

    @Autowired
    public final void setDigitalMediaBO(DigitalMediaBO digitalMediaBO) {
        this.digitalMediaBO = digitalMediaBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }
}
