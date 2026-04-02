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
 ******************************************************************************/
package biblivre.cataloging;

import biblivre.core.file.DiskFile;
import biblivre.core.translations.TranslationBO;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.marc4j.marc.*;
import org.marc4j.marc.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchResultsExcelExporter {
    private final TranslationBO translationBO;

    private static final String MULTI_VALUE_SEPARATOR = " | ";

    @Autowired
    public SearchResultsExcelExporter(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }

    public DiskFile export(List<RecordDTO> records, String language) throws IOException {
        if (records == null || records.isEmpty()) {
            return null;
        }

        TreeSet<String> columnKeys = new TreeSet<>(this::compareColumnKeys);
        for (RecordDTO dto : records) {
            Record marc = dto.getRecord();
            if (marc == null) {
                continue;
            }
            collectColumnKeys(marc, columnKeys);
        }

        if (columnKeys.isEmpty()) {
            return null;
        }

        List<String> columns = new ArrayList<>(columnKeys);

        File file =
                File.createTempFile(
                        "biblivre_search_export_",
                        ".xlsx",
                        new File(System.getProperty("java.io.tmpdir")));

        try (XSSFWorkbook workbook = new XSSFWorkbook();
                OutputStream out = Files.newOutputStream(file.toPath())) {
            var sheet = workbook.createSheet("search_results");
            var header = sheet.createRow(0);
            for (int c = 0; c < columns.size(); c++) {
                var i18n = translationBO.get(language);

                String cellValue = columns.get(c);

                var firstIndexOfDollar = cellValue.indexOf('$');

                var datafield = cellValue.substring(0, firstIndexOfDollar);

                var subfield = cellValue.substring(firstIndexOfDollar + 1);

                header.createCell(c)
                        .setCellValue(
                                i18n.getText(
                                        "marc.bibliographic.datafield."
                                                + datafield
                                                + ".subfield."
                                                + subfield));
            }

            int rowIndex = 1;
            for (RecordDTO dto : records) {
                Record marc = dto.getRecord();
                if (marc == null) {
                    continue;
                }
                Map<String, String> rowValues = buildRowValues(marc);
                var row = sheet.createRow(rowIndex++);
                for (int c = 0; c < columns.size(); c++) {
                    String v = rowValues.get(columns.get(c));
                    if (StringUtils.isNotBlank(v)) {
                        row.createCell(c).setCellValue(v);
                    }
                }
            }

            workbook.write(out);
        }

        DiskFile diskFile =
                new DiskFile(
                        file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        diskFile.setName("biblivre_search_export_" + stamp + ".xlsx");
        return diskFile;
    }

    private static void collectColumnKeys(Record marc, Set<String> keys) {
        for (VariableField vf : marc.getVariableFields()) {
            if (vf instanceof DataField df) {
                for (Subfield sf : df.getSubfields()) {
                    keys.add(df.getTag() + "$" + sf.getCode());
                }
            }
        }
    }

    private Map<String, String> buildRowValues(Record marc) {
        Map<String, List<String>> acc = new LinkedHashMap<>();
        for (VariableField vf : marc.getVariableFields()) {
            if (vf instanceof ControlField cf) {
                acc.computeIfAbsent(cf.getTag(), k -> new ArrayList<>()).add(cf.getData());
            } else if (vf instanceof DataField df) {
                for (Subfield sf : df.getSubfields()) {
                    String key = df.getTag() + "$" + sf.getCode();
                    acc.computeIfAbsent(key, k -> new ArrayList<>()).add(sf.getData());
                }
            }
        }
        Map<String, String> row = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> e : acc.entrySet()) {
            row.put(e.getKey(), String.join(MULTI_VALUE_SEPARATOR, e.getValue()));
        }
        return row;
    }

    private int compareColumnKeys(String a, String b) {
        boolean aData = a.indexOf('$') >= 0;
        boolean bData = b.indexOf('$') >= 0;
        if (aData != bData) {
            return aData ? 1 : -1;
        }
        if (!aData) {
            return a.compareTo(b);
        }
        int dollarA = a.indexOf('$');
        int dollarB = b.indexOf('$');
        String tagA = a.substring(0, dollarA);
        String tagB = b.substring(0, dollarB);
        int cmp = Integer.compare(Integer.parseInt(tagA), Integer.parseInt(tagB));
        if (cmp != 0) {
            return cmp;
        }
        return Character.compare(a.charAt(dollarA + 1), b.charAt(dollarB + 1));
    }
}
