package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.*;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.file.DiskFile;
import biblivre.marc.HumanReadableMarcReader;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.Record;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class RecordBOTest extends AbstractContainerDatabaseTest {

    public static final String humanReadableMarc =
            """
            000	01258nam a2200217 a 4500
            001	0000251
            005	20230413141548.880
            008	230413s|||| bl|||||||||||||||||por|u
            082	__|221|a981
            090	__|a981|bG633m
            100	__|aGomes, Laurentino|d1956-|eautor
            245	0_|a1822|bcomo um homem sábio, uma princesa triste e um escocês louco por dinheiro ajudaram D. Pedro a criar o Brasil : um país que tinha tudo para dar errado|cLaurentino Gomes
            246	__|aMil oitocentos e vinte e dois
            260	__|aRio de Janeiro (RJ)|bNova Fronteira|c2010
            300	__|a351 p., [20] p. de lâms.|bil.|c23 cm
            504	__|aBibliografia
            505	__|aContém notas.
            505	__|aÍndice onomástico.
            520	__|aEsta obra apresenta 22 capítulos intercalados por ilustrações de fatos e personagens da época da independência. Resultado de três anos de pesquisas, a obra cobre um período de quatorze anos, entre 1821, data do retorno da corte portuguesa de D. João VI a Lisboa, e 1834, ano da morte do imperador D. Pedro I. O livro procura explicar como o Brasil conseguiu manter a integridade do seu território e se firmar como nação independente em 1822.
            600	__|aPedro|bI,|cImperador do Brasil|d1798-1834
            651	__|aBrasil|xHistória|yImpério, 1822-1889
              """;

    @Test
    void createExportFile() {
        execute(
                () -> {
                    SchemaThreadLocal.setSchema("single");

                    BiblioRecordDTO record =
                            getBiblioRecordDTOFromHumanReadableMarc(humanReadableMarc);

                    BiblioRecordBO biblioRecordBO = getWiredBiblioRecordBO();

                    biblioRecordBO.saveOrUpdate(record, 0, null);

                    String accentedKeyword = "sábio";

                    SearchDTO search =
                            search(
                                    biblioRecordBO,
                                    """
                    {
                        "database":"main",
                        "material_type":"all",
                        "search_mode":"simple",
                        "search_terms":[
                            {
                                "query": "%s"
                            }
                        ]
                    }
                    """
                                            .formatted(accentedKeyword));

                    try (DiskFile diskFile =
                            biblioRecordBO.createExportFile(
                                    search.stream()
                                            .map(RecordDTO::getId)
                                            .collect(Collectors.toSet()),
                                    null)) {
                        ImportBO importBO = getWiredImportBO();

                        ImportDTO importDTO =
                                importBO.loadFromFile(() -> getByteArrayInputStream(diskFile));

                        List<RecordDTO> recordList = importDTO.getRecordList();

                        RecordDTO recordDTO = recordList.get(0);

                        Record marc4jRecord = recordDTO.getRecord();

                        marc4jRecord.getDataFields().stream()
                                .filter(dataField -> "245".equals(dataField.getTag()))
                                .map(dataField -> dataField.getSubfield('b').getData())
                                .forEach(
                                        value -> {
                                            assertEquals(
                                                    "como um homem sábio, uma princesa triste e um escocês louco por dinheiro ajudaram D. Pedro a criar o Brasil : um país que tinha tudo para dar errado",
                                                    value);
                                        });
                    }
                });
    }

    @NotNull
    private static ByteArrayInputStream getByteArrayInputStream(DiskFile diskFile)
            throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        diskFile.copy(byteArrayOutputStream);

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    @Test
    void search() {
        execute(
                () -> {
                    SchemaThreadLocal.setSchema("single");

                    BiblioRecordDTO record =
                            getBiblioRecordDTOFromHumanReadableMarc(
                                    """
                    000	00153nam a2200073 a 4500
                    001	0000383
                    005	20230725161639.272
                    008	230725s|||| bl|||||||||||||||||por|u
                    245	10|aÁgape
                    """);

                    BiblioRecordBO biblioRecordBO = getWiredBiblioRecordBO();

                    biblioRecordBO.saveOrUpdate(record, 0, null);

                    SearchDTO search =
                            search(
                                    biblioRecordBO,
                                    """
                    {
                        "database":"main",
                        "material_type":"all",
                        "search_mode":"simple",
                        "search_terms":[
                            {
                                "query": "Ágape"
                            }
                        ]
                    }
                    """);

                    assertEquals(1, search.size());
                });
    }

    public SearchDTO search(BiblioRecordBO biblioRecordBO, String queryJson) {
        return biblioRecordBO.search(new SearchQueryDTO(queryJson), null);
    }

    @NotNull
    private static BiblioRecordDTO getBiblioRecordDTOFromHumanReadableMarc(
            String humanReadableMarc) {
        HumanReadableMarcReader humanReadableMarcReader =
                new HumanReadableMarcReader(humanReadableMarc, MaterialType.BOOK, RecordStatus.NEW);

        BiblioRecordDTO record = new BiblioRecordDTO();

        record.setRecord(humanReadableMarcReader.next());

        return record;
    }
}
