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

import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.authorities.AuthorityRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.cataloging.search.SearchDAO;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.cataloging.vocabulary.VocabularyRecordBO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.SearchMode;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.DiskFile;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;
import biblivre.digitalmedia.DigitalMediaBO;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcStreamWriter;

public abstract class RecordBO extends AbstractBO {
    protected RecordDAO rdao;
    protected SearchDAO sdao;

    public static final int FULL = 1 << 0;
    public static final int MARC_INFO = 1 << 1;
    public static final int HOLDING_INFO = 1 << 2;
    public static final int HOLDING_LIST = 1 << 3;
    public static final int LENDING_INFO = 1 << 4;
    public static final int LENDING_LIST = 1 << 5;
    public static final int ATTACHMENTS_LIST = 1 << 6;

    public static final Pattern ID_PATTERN = Pattern.compile("id=(.*?)(&|$)");

    /** Class Factory */
    public static RecordBO getInstance(String schema, RecordType recordType) {
        if (recordType == null) {
            return null;
        }

        switch (recordType) {
            case BIBLIO:
                return BiblioRecordBO.getInstance(schema);
            case HOLDING:
                return HoldingBO.getInstance(schema);
            case AUTHORITIES:
                return AuthorityRecordBO.getInstance(schema);
            case VOCABULARY:
                return VocabularyRecordBO.getInstance(schema);
            default:
                return null;
        }
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

    public Map<Integer, RecordDTO> map(Set<Integer> ids) {
        return this.rdao.map(ids, getRecordType());
    }

    public Map<Integer, RecordDTO> map(Set<Integer> ids, int mask) {
        Map<Integer, RecordDTO> map = this.rdao.map(ids, getRecordType());

        for (RecordDTO dto : map.values()) {
            this.populateDetails(dto, mask);
        }

        return map;
    }

    public List<RecordDTO> list(int offset, int limit) {
        return this.rdao.list(offset, limit, getRecordType());
    }

    public List<RecordDTO> listByLetter(char letter, int order) {
        return this.populateDetails(this.rdao.listByLetter(letter, order, getRecordType()), RecordBO.MARC_INFO);
    }

    public boolean save(RecordDTO dto) {
        return this.rdao.save(dto);
    }

    public boolean update(RecordDTO dto) {
        return this.rdao.update(dto);
    }

    public boolean moveRecords(
            Set<Integer> ids,
            RecordDatabase recordDatabase,
            int modifiedBy,
            AuthorizationPoints authorizationPoints) {
        if (recordDatabase == RecordDatabase.PRIVATE || listContainsPrivateRecord(ids)) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        return this.rdao.moveRecords(ids, modifiedBy, recordDatabase, getRecordType());
    }

    public boolean listContainsPrivateRecord(Set<Integer> ids) {
        return this.rdao.listContainsPrivateRecord(ids, getRecordType());
    }

    public DiskFile createExportFile(Set<Integer> ids, AuthorizationPoints authorizationPoints) {
        if (listContainsPrivateRecord(ids)) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        Map<Integer, RecordDTO> records = this.map(ids);

        FileOutputStream out = null;

        try {
            File file = File.createTempFile("biblivre", ".mrc");

            out = new FileOutputStream(file);

            MarcStreamWriter writer = new MarcStreamWriter(out);

            for (RecordDTO dto : records.values()) {
                writer.write(dto.getRecord());
            }

            writer.close();

            return new DiskFile(file, "x-download");
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(out);
        }

        return null;
    }

    public boolean delete(RecordDTO dto) {
        return this.rdao.delete(dto);
    }

    public Integer count() {
        return this.count(null);
    }

    private Integer count(SearchDTO search) {
        return this.rdao.count(search);
    }

    public boolean search(SearchDTO search) {
        SearchMode searchMode = search.getSearchMode();

        boolean isNewSearch = (search.getId() == null);

        if (isNewSearch) {
            if (!this.sdao.createSearch(search)) {
                return false;
            }
        }

        switch (searchMode) {
            case SIMPLE:
                if (!this.sdao.populateSimpleSearch(search, !isNewSearch)) {
                    return false;
                }

                break;

            case ADVANCED:
                if (!this.sdao.populateAdvancedSearch(search, !isNewSearch)) {
                    return false;
                }

                break;

            case LIST_ALL:
                break;
        }

        return this.paginateSearch(search);
    }

    public boolean paginateSearch(SearchDTO search, AuthorizationPoints authorizationPoints) {
        if (search.getQuery().getDatabase() == RecordDatabase.PRIVATE) {
            this.authorize(
                    "cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        return paginateSearch(search);
    }

    public boolean paginateSearch(SearchDTO search) {
        if (search.getQuery().isHoldingSearch()) {
            HoldingBO hbo = (HoldingBO) RecordBO.getInstance(this.getSchema(), RecordType.HOLDING);
            return hbo.paginateHoldingSearch(search);
        }

        Map<Integer, Integer> groupCount = this.rdao.countSearchResults(search);
        Integer count = groupCount.get(search.getIndexingGroup());

        if (count == null || count == 0) {
            return false;
        }

        List<RecordDTO> list = this.rdao.getSearchResults(search);

        search.getPaging().setRecordCount(count);
        search.setIndexingGroupCount(groupCount);

        for (RecordDTO rdto : list) {
            this.populateDetails(rdto, RecordBO.MARC_INFO | RecordBO.HOLDING_INFO);
            search.add(rdto);
        }

        return true;
    }

    public SearchDTO getSearch(Integer searchId) {
        SearchDTO search = this.sdao.getSearch(searchId);

        if (search != null) {
            if (search.getPaging() == null) {
                search.setPaging(new PagingDTO());
            }

            PagingDTO paging = search.getPaging();
            paging.setRecordsPerPage(
                    Configurations.getPositiveInt(
                            this.getSchema(), Constants.CONFIG_SEARCH_RESULTS_PER_PAGE, 20));
            paging.setRecordLimit(
                    Configurations.getPositiveInt(
                            this.getSchema(), Constants.CONFIG_SEARCH_RESULT_LIMIT, 2000));
        }

        return search;
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
                this.rdao.phraseAutocomplete(datafield, subfield, searchTerms, 10, true, getRecordType());
        List<String> listB =
                this.rdao.phraseAutocomplete(datafield, subfield, searchTerms, 5, false, getRecordType());

        listA.addAll(listB);

        return listA;
    }

    public DTOCollection<AutocompleteDTO> recordAutocomplete(
            String datafield, String subfield, String query) {
        String[] searchTerms = TextUtils.prepareAutocomplete(query);

        DTOCollection<AutocompleteDTO> listA =
                this.rdao.recordAutocomplete(datafield, subfield, searchTerms, 10, true, getRecordType());
        DTOCollection<AutocompleteDTO> listB =
                this.rdao.recordAutocomplete(datafield, subfield, searchTerms, 5, false, getRecordType());

        listA.addAll(listB);

        return listA;
    }

    public void addAttachment(int recordId, String uri, String description, Integer userId) {
        RecordDTO recordDTO = get(recordId);

        recordDTO.addAttachment(uri, description);

        recordDTO.setModifiedBy(userId);

        update(recordDTO);
    }

    public RecordDTO removeAttachment(
            Integer recordId, String uri, String description, Integer userId) {
        RecordDTO dto = this.get(recordId);

        dto.removeAttachment(uri, description);

        dto.setModifiedBy(userId);

        this.update(dto);

        // Check if the file is in Biblivre's DB and try to delete it
        try {
            Matcher matcher = RecordBO.ID_PATTERN.matcher(uri);
            if (matcher.find()) {
                String encodedId = matcher.group(1);
                String fileId = "";
                String fileName = "";
                String decodedId = new String(Base64.getDecoder().decode(encodedId));
                String[] splitId = decodedId.split(":");
                if (splitId.length == 2 && StringUtils.isNumeric(splitId[0])) {
                    fileId = splitId[0];
                    fileName = splitId[1];
                }

                // Try to remove the file from Biblivre DB
                DigitalMediaBO dmbo = DigitalMediaBO.getInstance(this.getSchema());
                dmbo.delete(Integer.valueOf(fileId), fileName);
            }
        } catch (Exception e) {
        }

        return dto;
    }

    public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
        return this.rdao.saveFromBiblivre3(dtoList);
    }

    public abstract void populateDetails(RecordDTO record, int mask);

    public abstract boolean isDeleatable(HoldingDTO holding) throws ValidationException;

    public abstract RecordType getRecordType();

    public RecordDTO open(int id, AuthorizationPoints authorizationPoints) {
        RecordDTO dto = get(id);

        if (dto == null) {
            return null;
        }

        if (dto.getRecordDatabase() == RecordDatabase.PRIVATE) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        populateDetails(
                dto,
                RecordBO.MARC_INFO
                        | RecordBO.HOLDING_INFO
                        | RecordBO.HOLDING_LIST
                        | RecordBO.LENDING_INFO);
        return dto;
    }

    public boolean saveOrUpdate(
            RecordDTO recordDTO, int loggedUserId, AuthorizationPoints authorizationPoints) {
        if (recordDTO.getRecordDatabase() == RecordDatabase.PRIVATE) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        boolean success = false;

        if (recordDTO.isNew()) {
            recordDTO.setCreatedBy(loggedUserId);

            success = save(recordDTO);
        } else {
            recordDTO.setModifiedBy(loggedUserId);

            success = update(recordDTO);
        }

        return success;
    }

    public SearchDTO search(
            SearchQueryDTO searchQuery,
            RecordType recordType,
            AuthorizationPoints authorizationPoints) {
        if (searchQuery.getDatabase() == RecordDatabase.PRIVATE) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        SearchDTO search = new SearchDTO(recordType);

        PagingDTO paging = _newConfiguredPagingInstance();

        search.setPaging(paging);

        search.setQuery(searchQuery);

        search.setSort(IndexingGroups.getDefaultSortableGroupId(schema, recordType));

        search(search);

        paging.endTimer();

        return search;
    }

    public boolean delete(
            RecordDTO dto, int loggedUserId, AuthorizationPoints authorizationPoints) {
        RecordDatabase recordDatabase = dto.getRecordDatabase();

        if (recordDatabase == RecordDatabase.PRIVATE) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        dto.setModifiedBy(loggedUserId);

        boolean success = false;

        if (recordDatabase == RecordDatabase.TRASH) {
            success = this.rdao.delete(dto);
        } else {
            Set<Integer> ids = new HashSet<>();

            ids.add(dto.getId());

            success = this.rdao.moveRecords(ids, loggedUserId, RecordDatabase.TRASH, getRecordType());
        }
        return success;
    }

    private PagingDTO _newConfiguredPagingInstance() {
        PagingDTO paging = new PagingDTO();

        paging.setRecordsPerPage(
                Configurations.getPositiveInt(
                        schema, Constants.CONFIG_SEARCH_RESULTS_PER_PAGE, 20));

        paging.setRecordLimit(
                Configurations.getPositiveInt(schema, Constants.CONFIG_SEARCH_RESULT_LIMIT, 2000));

        paging.setPage(1);

        return paging;
    }

    public int count(SearchDTO searchDTO, AuthorizationPoints authorizationPoints) {
        if (searchDTO.getQuery().getDatabase() == RecordDatabase.PRIVATE) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        return count(searchDTO);
    }
}
