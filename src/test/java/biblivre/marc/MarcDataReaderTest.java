package biblivre.marc;

import static org.junit.jupiter.api.Assertions.*;

import biblivre.cataloging.BriefTabFieldDTO;
import biblivre.cataloging.BriefTabFieldFormatDTO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import utils.MarcUtils;

class MarcDataReaderTest {
    public static final String _1984_HUMAN_READABLE_MARC =
            """
            000	03404nam a2200181 a 4500
            001	0000291
            005	20230413160331.192
            008	230413s|||| bl|||||||||||||||||por|u
            082	__|221|a823
            090	__|a823|bO79m
            100	__|aOrwell, George|d1903-1950|qEric Arthur Blair
            245	0_|a1984|cGeorge Orwell ; traduzido por Antônio Xerxenesky; ilustrado por Rafael Coutinho
            260	__|aRio de Janeiro|bAntofágica|c2021
            300	__|a502 p.|c21 cm
            505	__|aInclui apêndice e dados biográficos do autor.
            520	__|1https://www.companhiadasletras.com.br/livro/9788535914849/1984?idtag=7ec82fe8-e709-4f1a-9969-7d018c0785e5&gclid=CjwKCAjwhNWZBhB_EiwAPzlhNj1KuVNcPHlY2e2XLzimDGV1WF2cpccn4yWUAaepQiE5PILl_lj3hRoCPE0QAvD_BwE|a"Winston, herói de 1984, último romance de George Orwell, vive aprisionado na engrenagem totalitária de uma sociedade completamente dominada pelo Estado, onde tudo é feito coletivamente, mas cada qual vive sozinho. Ninguém escapa à vigilância do Grande Irmão, a mais famosa personificação literária de um poder cínico e cruel ao infinito, além de vazio de sentido histórico. De fato, a ideologia do Partido dominante em Oceânia não visa nada de coisa alguma para ninguém, no presente ou no futuro. O'Brien, hierarca do Partido, é quem explica a Winston que "só nos interessa o poder em si. Nem riqueza, nem luxo, nem vida longa, nem felicidade: só o poder pelo poder, poder puro". Quando foi publicada em 1949, essa assustadora distopia datada de forma arbitrária num futuro perigosamente próximo logo experimentaria um imenso sucesso de público. Seus principais ingredientes - um homem sozinho desafiando uma tremenda ditadura; sexo furtivo e libertador; horrores letais - atraíram leitores de todas as idades, à esquerda e à direita do espectro político, com maior ou menor grau de instrução. À parte isso, a escrita translúcida de George Orwell, os personagens fortes, traçados a carvão por um vigoroso desenhista de personalidades, a trama seca e crua e o tom de sátira sombria garantiram a entrada precoce de 1984 no restrito panteão dos grandes clássicos modernos. Algumas das ideias centrais do livro dão muito o que pensar até hoje, como a contraditória Novafala imposta pelo Partido para renomear as coisas, as instituições e o próprio mundo, manipulando ao infinito a realidade. Afinal, quem não conhece hoje em dia "ministérios da defesa" dedicados a promover ataques bélicos a outros países, da mesma forma que, no livro de Orwell, o "Ministério do Amor" é o local onde Winston será submetido às mais bárbaras torturas nas mãos de seu suposto amigo O'Brien. Muitos leram 1984 como uma crítica devastadora aos belicosos totalitarismos nazifascistas da Europa, de cujos terríveis crimes o mundo ainda tentava se recuperar quando o livro veio a lume. Nos Estados Unidos, foi visto como uma fantasia de horror quase cômico voltada contra o comunismo da hoje extinta União Soviética, então sob o comando de Stálin e seu Partido único e inquestionável. No entanto, superando todas as conjunturas históricas - e até mesmo a data futurista do título -, a obra magistral de George Orwell ainda se impõe como uma poderosa reflexão ficcional sobre os excessos delirantes, mas perfeitamente possíveis, de qualquer forma de poder incontestado, seja onde for." 02
            650	__|aRomance inglês|ySéculo 20
            655	__|aHistórias antiutópicas
            """;

    @Test
    void getFieldList() {
        BiblioRecordDTO biblioRecord =
                MarcUtils.getBiblioRecordDTOFromHumanReadableMarc(_1984_HUMAN_READABLE_MARC);

        MarcDataReader marcDataReader = new MarcDataReader(biblioRecord.getRecord());

        List<BriefTabFieldDTO> fieldList =
                marcDataReader.getFieldList(
                        List.of(
                                new BriefTabFieldFormatDTO(
                                        MarcConstants.AUTHOR_PERSONAL_NAME,
                                        "${a}_{ - }${d}_{ }(${q})")));

        assertEquals("Orwell, George - 1903-1950 (Eric Arthur Blair)", fieldList.get(0).getValue());
    }
}
