package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.file.DiskFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import utils.MarcUtils;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
class RecordBOTest extends AbstractContainerDatabaseTest {
    @Autowired private BiblioRecordBO biblioRecordBO;
    @Autowired private ImportBO importBO;

    @BeforeEach
    void dropAllRecords() {
        SchemaThreadLocal.setSchema("single");

        SearchDTO search =
                search(
                        biblioRecordBO,
                        """
                                {"database":"main","material_type":"all","search_mode":"list_all"}
                                """);

        search.forEach(biblioRecordBO::delete);
    }

    public static final String _1822_HUMAN_READABLE_MARC =
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

    public static final String _1984_HUMAN_READABLE_MARC =
            """
                    000	03404nam a2200181 a 4500
                    001	0000291
                    005	20230413160331.192
                    008	230413s|||| bl|||||||||||||||||por|u
                    082	__|221|a823
                    090	__|a823|bO79m
                    100	__|aOrwell, George|d1903-1950
                    245	0_|a1984|cGeorge Orwell ; traduzido por Antônio Xerxenesky; ilustrado por Rafael Coutinho
                    260	__|aRio de Janeiro|bAntofágica|c2021
                    300	__|a502 p.|c21 cm
                    505	__|aInclui apêndice e dados biográficos do autor.
                    520	__|1https://www.companhiadasletras.com.br/livro/9788535914849/1984?idtag=7ec82fe8-e709-4f1a-9969-7d018c0785e5&gclid=CjwKCAjwhNWZBhB_EiwAPzlhNj1KuVNcPHlY2e2XLzimDGV1WF2cpccn4yWUAaepQiE5PILl_lj3hRoCPE0QAvD_BwE|a"Winston, herói de 1984, último romance de George Orwell, vive aprisionado na engrenagem totalitária de uma sociedade completamente dominada pelo Estado, onde tudo é feito coletivamente, mas cada qual vive sozinho. Ninguém escapa à vigilância do Grande Irmão, a mais famosa personificação literária de um poder cínico e cruel ao infinito, além de vazio de sentido histórico. De fato, a ideologia do Partido dominante em Oceânia não visa nada de coisa alguma para ninguém, no presente ou no futuro. O'Brien, hierarca do Partido, é quem explica a Winston que "só nos interessa o poder em si. Nem riqueza, nem luxo, nem vida longa, nem felicidade: só o poder pelo poder, poder puro". Quando foi publicada em 1949, essa assustadora distopia datada de forma arbitrária num futuro perigosamente próximo logo experimentaria um imenso sucesso de público. Seus principais ingredientes - um homem sozinho desafiando uma tremenda ditadura; sexo furtivo e libertador; horrores letais - atraíram leitores de todas as idades, à esquerda e à direita do espectro político, com maior ou menor grau de instrução. À parte isso, a escrita translúcida de George Orwell, os personagens fortes, traçados a carvão por um vigoroso desenhista de personalidades, a trama seca e crua e o tom de sátira sombria garantiram a entrada precoce de 1984 no restrito panteão dos grandes clássicos modernos. Algumas das ideias centrais do livro dão muito o que pensar até hoje, como a contraditória Novafala imposta pelo Partido para renomear as coisas, as instituições e o próprio mundo, manipulando ao infinito a realidade. Afinal, quem não conhece hoje em dia "ministérios da defesa" dedicados a promover ataques bélicos a outros países, da mesma forma que, no livro de Orwell, o "Ministério do Amor" é o local onde Winston será submetido às mais bárbaras torturas nas mãos de seu suposto amigo O'Brien. Muitos leram 1984 como uma crítica devastadora aos belicosos totalitarismos nazifascistas da Europa, de cujos terríveis crimes o mundo ainda tentava se recuperar quando o livro veio a lume. Nos Estados Unidos, foi visto como uma fantasia de horror quase cômico voltada contra o comunismo da hoje extinta União Soviética, então sob o comando de Stálin e seu Partido único e inquestionável. No entanto, superando todas as conjunturas históricas - e até mesmo a data futurista do título -, a obra magistral de George Orwell ainda se impõe como uma poderosa reflexão ficcional sobre os excessos delirantes, mas perfeitamente possíveis, de qualquer forma de poder incontestado, seja onde for." 02
                    650	__|aRomance inglês|ySéculo 20
                    655	__|aHistórias antiutópicas
                    """;

    @Test
    void createExportFile() {
        SchemaThreadLocal.setSchema("single");

        BiblioRecordDTO record =
                MarcUtils.getBiblioRecordDTOFromHumanReadableMarc(_1822_HUMAN_READABLE_MARC);

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
                                            "query": "sábio"
                                        }
                                    ]
                                }
                                """);

        try (DiskFile diskFile =
                biblioRecordBO.createExportFile(
                        search.stream().map(RecordDTO::getId).collect(Collectors.toSet()), null)) {

            ImportDTO importDTO =
                    importBO.loadFromFile(
                            () -> {
                                ByteArrayOutputStream byteArrayOutputStream =
                                        new ByteArrayOutputStream();

                                diskFile.copy(byteArrayOutputStream);

                                return new ByteArrayInputStream(
                                        byteArrayOutputStream.toByteArray());
                            });

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
    }

    @Test
    void search() {
        BiblioRecordDTO record =
                MarcUtils.getBiblioRecordDTOFromHumanReadableMarc(
                        """
                                000	00153nam a2200073 a 4500
                                001	0000383
                                005	20230725161639.272
                                008	230725s|||| bl|||||||||||||||||por|u
                                245	10|aÁgape
                                """);

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
    }

    @Test
    void searchAdvancedWithASingleAndNotOperator() {
        BiblioRecordDTO record =
                MarcUtils.getBiblioRecordDTOFromHumanReadableMarc(_1822_HUMAN_READABLE_MARC);

        biblioRecordBO.saveOrUpdate(record, 0, null);

        SearchDTO search =
                search(
                        biblioRecordBO,
                        """
                                {
                                    "database": "main",
                                    "material_type": "all",
                                    "search_mode": "advanced",
                                    "search_terms": [
                                        {
                                            "query": "princesa",
                                            "field": "3",
                                            "operator": "AND_NOT"
                                        }
                                    ]
                                }
                                """);

        assertEquals(0, search.size());
    }

    @Test
    void searchAdvancedAndNot() {
        biblioRecordBO.saveOrUpdate(
                MarcUtils.getBiblioRecordDTOFromHumanReadableMarc(_1822_HUMAN_READABLE_MARC),
                0,
                null);

        biblioRecordBO.saveOrUpdate(
                MarcUtils.getBiblioRecordDTOFromHumanReadableMarc(_1984_HUMAN_READABLE_MARC),
                0,
                null);

        SearchDTO search =
                search(
                        biblioRecordBO,
                        """
                                {
                                    "database": "main",
                                    "material_type": "all",
                                    "search_mode": "advanced",
                                    "search_terms": [
                                        {
                                            "query": "princesa",
                                            "field": "3",
                                            "operator": "AND_NOT"
                                        },
                                        {
                                            "query": "triste",
                                            "field": "3",
                                            "operator": "AND"
                                        }
                                    ]
                                }
                                """);

        assertEquals(0, search.size());
    }

    @Test
    void searchAdvancedOr() {
        biblioRecordBO.saveOrUpdate(
                MarcUtils.getBiblioRecordDTOFromHumanReadableMarc(_1822_HUMAN_READABLE_MARC),
                0,
                null);

        biblioRecordBO.saveOrUpdate(
                MarcUtils.getBiblioRecordDTOFromHumanReadableMarc(_1984_HUMAN_READABLE_MARC),
                0,
                null);

        SearchDTO search =
                search(
                        biblioRecordBO,
                        """
                                {
                                     "database": "main",
                                     "material_type": "all",
                                     "search_mode": "advanced",
                                     "search_terms": [
                                         {
                                             "query": "princesa",
                                             "field": "3",
                                             "operator": "AND"
                                         },
                                         {
                                             "query": "1984",
                                             "field": "3",
                                             "operator": "OR"
                                         }
                                     ]
                                 }
                                        """);

        assertEquals(2, search.size());
    }

    public SearchDTO search(BiblioRecordBO biblioRecordBO, String queryJson) {
        return biblioRecordBO.search(new SearchQueryDTO(queryJson), null);
    }
}
