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
package biblivre.cataloging.labels;

import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.DiskFile;
import biblivre.labels.print.LabelPrintDTO;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.marc4j.marc.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private BiblioRecordBO biblioRecordBO;
    private HoldingBO holdingBO;

    public void createPdf(ExtendedRequest request, ExtendedResponse response) {

        String printId = UUID.randomUUID().toString();

        LabelPrintDTO print = new LabelPrintDTO();
        String idList = request.getString("id_list");

        String[] idArray = idList.split(",");
        Set<Integer> ids = new HashSet<>();
        try {
            for (String s : idArray) {
                ids.add(Integer.valueOf(s));
            }
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
        }

        print.setIds(ids);
        print.setOffset(request.getInteger("offset"));
        print.setWidth(request.getFloat("width"));
        print.setHeight(request.getFloat("height"));
        print.setColumns(request.getInteger("columns"));
        print.setRows(request.getInteger("rows"));
        print.setModel(request.getString("model"));

        request.setScopedSessionAttribute(printId, print);

        try {
            put("uuid", printId);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void downloadPdf(ExtendedRequest request, ExtendedResponse response) {

        String printId = request.getString("id");
        LabelPrintDTO dto = (LabelPrintDTO) request.getScopedSessionAttribute(printId);

        Map<Integer, RecordDTO> hdto = holdingBO.map(dto.getIds(), RecordBO.MARC_INFO);

        List<LabelDTO> labels = new ArrayList<>();
        for (RecordDTO rdto : hdto.values()) {
            HoldingDTO holding = (HoldingDTO) rdto;

            LabelDTO label = new LabelDTO();

            label.setId(rdto.getId());
            label.setAccessionNumber(holding.getAccessionNumber());

            MarcDataReader dataReader = getBibliographicRecordMarcDataReader(holding);

            label.setAuthor(StringUtils.defaultString(dataReader.getAuthorName(false)));
            label.setTitle(StringUtils.defaultString(dataReader.getTitle(false)));

            Record holdingRecord = MarcUtils.iso2709ToRecord(holding.getIso2709());
            dataReader = new MarcDataReader(holdingRecord);
            label.setLocationA(StringUtils.defaultString(dataReader.getLocation()));
            label.setLocationB(StringUtils.defaultString(dataReader.getLocationB()));
            label.setLocationC(StringUtils.defaultString(dataReader.getLocationC()));
            label.setLocationD(StringUtils.defaultString(dataReader.getLocationD()));

            labels.add(label);
        }

        final DiskFile exportFile = holdingBO.printLabelsToPDF(labels, dto);
        holdingBO.markAsPrinted(dto.getIds());

        this.setFile(exportFile);

        this.setCallback(exportFile::delete);
    }

    private MarcDataReader getBibliographicRecordMarcDataReader(HoldingDTO holding) {
        BiblioRecordDTO biblio = (BiblioRecordDTO) biblioRecordBO.get(holding.getRecordId());
        Record biblioRecord = MarcUtils.iso2709ToRecord(biblio.getIso2709());
        return new MarcDataReader(biblioRecord);
    }

    @Autowired
    public void setBiblioRecordBO(BiblioRecordBO biblioRecordBO) {
        this.biblioRecordBO = biblioRecordBO;
    }

    @Autowired
    public void setHoldingBO(HoldingBO holdingBO) {
        this.holdingBO = holdingBO;
    }
}
