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

import biblivre.administration.indexing.IndexingBO;
import biblivre.cataloging.authorities.AuthorityRecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.ImportEncoding;
import biblivre.cataloging.enums.ImportFormat;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.vocabulary.VocabularyRecordDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.MemoryFile;
import biblivre.core.utils.Constants;
import biblivre.marc.HumanReadableMarcReader;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import biblivre.z3950.Z3950AddressDTO;
import biblivre.z3950.Z3950BO;
import biblivre.z3950.Z3950RecordDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.marc4j.marc.Record;

public class Handler extends AbstractHandler {
    private Map<RecordType, RecordBO> recordBOs = new HashMap<>();
    private IndexingBO indexingBO;
	private ImportBO importBO;
	private Z3950BO z3950BO;

    public void importUpload(ExtendedRequest request, ExtendedResponse response) {

        MemoryFile file = request.getFile("file");

        ImportFormat format =
                request.getEnum(ImportFormat.class, "format", ImportFormat.AUTO_DETECT);
        ImportEncoding encoding =
                request.getEnum(ImportEncoding.class, "encoding", ImportEncoding.AUTO_DETECT);

        ImportDTO list = importBO.loadFromFile(file, format, encoding);

        if (list != null) {
            List<String> isbnList = new ArrayList<>();
            List<String> issnList = new ArrayList<>();
            List<String> isrcList = new ArrayList<>();

            for (RecordDTO dto : list.getRecordList()) {
                if (dto instanceof BiblioRecordDTO) {
                    BiblioRecordDTO rdto = (BiblioRecordDTO) dto;

                    if (StringUtils.isNotBlank(rdto.getIsbn())) {
                        isbnList.add(rdto.getIsbn());
                    } else if (StringUtils.isNotBlank(rdto.getIssn())) {
                        issnList.add(rdto.getIssn());
                    } else if (StringUtils.isNotBlank(rdto.getIsrc())) {
                        isrcList.add(rdto.getIsrc());
                    }
                }
                // TODO: Completar para autoridades e vocabulário
            }

            if (isbnList.size() > 0) {
                list.setFoundISBN(indexingBO.searchExactTerms(RecordType.BIBLIO, 5, isbnList));
            }

            if (issnList.size() > 0) {
                list.setFoundISSN(indexingBO.searchExactTerms(RecordType.BIBLIO, 6, issnList));
            }

            if (isrcList.size() > 0) {
                list.setFoundISRC(indexingBO.searchExactTerms(RecordType.BIBLIO, 7, isrcList));
            }
        }

        try {
            if (list == null) {
                this.setMessage(ActionResult.WARNING, "cataloging.import.error.invalid_file");
            } else if (list.getSuccess() == 0) {
                this.setMessage(ActionResult.WARNING, "cataloging.import.error.no_record_found");
            } else {
                this.json.putOpt("data", list.toJSONObject());
            }
        } catch (JSONException e) {
        }
    }

    public void importSearch(ExtendedRequest request, ExtendedResponse response) {

        Integer id = request.getInteger("search_server");
        String attribute = request.getString("search_attribute");
        String value = request.getString("search_query");

        Z3950AddressDTO server = z3950BO.findById(id);

        List<Z3950AddressDTO> serverList = new ArrayList<>();
        serverList.add(server);
        Pair<String, String> search = Pair.of(attribute, value);

        List<Z3950RecordDTO> recordList = z3950BO.search(serverList, search);

        ImportDTO list = importBO.readFromZ3950Results(recordList);

        if (list != null) {
            List<String> isbnList = new ArrayList<>();
            List<String> issnList = new ArrayList<>();
            List<String> isrcList = new ArrayList<>();

            for (RecordDTO dto : list.getRecordList()) {
                if (dto instanceof BiblioRecordDTO) {
                    BiblioRecordDTO rdto = (BiblioRecordDTO) dto;

                    if (StringUtils.isNotBlank(rdto.getIsbn())) {
                        isbnList.add(rdto.getIsbn());
                    } else if (StringUtils.isNotBlank(rdto.getIssn())) {
                        issnList.add(rdto.getIssn());
                    } else if (StringUtils.isNotBlank(rdto.getIsrc())) {
                        isrcList.add(rdto.getIsrc());
                    }
                }
            }

            if (isbnList.size() > 0) {
                list.setFoundISBN(indexingBO.searchExactTerms(RecordType.BIBLIO, 5, isbnList));
            }

            if (issnList.size() > 0) {
                list.setFoundISSN(indexingBO.searchExactTerms(RecordType.BIBLIO, 6, issnList));
            }

            if (isrcList.size() > 0) {
                list.setFoundISRC(indexingBO.searchExactTerms(RecordType.BIBLIO, 7, isrcList));
            }
        }

        try {
            if (list == null) {
                this.setMessage(ActionResult.WARNING, "cataloging.import.error.invalid_file");
            } else if (list.getSuccess() == 0) {
                this.setMessage(ActionResult.WARNING, "cataloging.import.error.no_record_found");
            } else {
                this.json.putOpt("data", list.toJSONObject());
            }
        } catch (JSONException e) {
        }
    }

    public void parseMarc(ExtendedRequest request, ExtendedResponse response) {
        String marc = request.getString("marc");

        RecordDTO dto = null;
        try {
            HumanReadableMarcReader humanReadableMarcReader =
                    new HumanReadableMarcReader(marc, RecordStatus.NEW);

            Record record = humanReadableMarcReader.next();

            dto = importBO.dtoFromRecord(record);
            BiblioRecordDTO rdto = ((BiblioRecordDTO) dto);

            if (StringUtils.isNotBlank(rdto.getIsbn())) {
                List<String> search =
                        indexingBO.searchExactTerm(RecordType.BIBLIO, 5, rdto.getIsbn());
                this.json.putOpt("isbn", !search.isEmpty());
            } else if (StringUtils.isNotBlank(rdto.getIssn())) {
                List<String> search =
                        indexingBO.searchExactTerm(RecordType.BIBLIO, 6, rdto.getIssn());
                this.json.putOpt("issn", !search.isEmpty());
            } else if (StringUtils.isNotBlank(rdto.getIsrc())) {
                List<String> search =
                        indexingBO.searchExactTerm(RecordType.BIBLIO, 7, rdto.getIsrc());
                this.json.putOpt("isrc", !search.isEmpty());
            }

        } catch (Exception e) {
        }

        try {
            if (dto == null) {
                this.setMessage(ActionResult.WARNING, "cataloging.import.error.invalid_marc");
            } else {
                this.json.putOpt("data", dto.toJSONObject());
            }
        } catch (JSONException e) {
        }
    }

    public void saveImport(ExtendedRequest request, ExtendedResponse response) {

        int start = request.getInteger("start", 1);
        int end =
                request.getInteger(
                        "end", Configurations.getInt(Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
        Set<Integer> successIds = new HashSet<>();
        Set<Integer> failedIds = new HashSet<>();

        for (int i = start; i <= end; i++) {
            String marc = request.getString("marc_" + i);
            RecordType recordType = request.getEnum(RecordType.class, "record_type_" + i);

            if (recordType == null) {
                continue;
            }

            RecordBO bo = recordBOs.get(recordType);

            RecordDTO dto = null;

            switch (recordType) {
                case BIBLIO:
                    dto = new BiblioRecordDTO();
                    break;
                case AUTHORITIES:
                    dto = new AuthorityRecordDTO();
                    break;
                case VOCABULARY:
                    dto = new VocabularyRecordDTO();
                    break;
                default:
                    dto = new RecordDTO();
            }

            Record record = null;
            try {
                HumanReadableMarcReader humanReadableMarcReader =
                        new HumanReadableMarcReader(marc, RecordStatus.NEW);

                record = humanReadableMarcReader.next();
            } catch (Exception e) {
                failedIds.add(i);
                this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
                continue;
            }

            if (record == null) {
                continue;
            }

            dto.setRecord(record);
            dto.setMaterialType(MaterialType.fromRecord(record));
            dto.setRecordDatabase(RecordDatabase.WORK);
            dto.setCreatedBy(request.getLoggedUserId());

            if (bo.save(dto)) {
                successIds.add(i);
            } else {
                failedIds.add(i);
            }
        }

        if (!successIds.isEmpty()) {
            this.setMessage(ActionResult.SUCCESS, "cataloging.import.save.success");
        } else {
            this.setMessage(ActionResult.WARNING, "cataloging.import.save.failed");
        }

        try {
            for (Integer id : successIds) {
                this.json.append("saved", id);
            }
            for (Integer id : failedIds) {
                this.json.append("failed", id);
            }
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_json");
        }
    }

	public void setRecordBOs(Map<RecordType, RecordBO> recordBOs) {
		this.recordBOs = recordBOs;
	}

	public void setIndexingBO(IndexingBO indexingBO) {
		this.indexingBO = indexingBO;
	}

	public void setImportBO(ImportBO importBO) {
		this.importBO = importBO;
	}
}
