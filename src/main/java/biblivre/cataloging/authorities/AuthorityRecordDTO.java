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
package biblivre.cataloging.authorities;

import biblivre.cataloging.RecordDTO;
import biblivre.marc.MarcDataReader;
import java.io.Serial;
import java.util.Arrays;
import org.json.JSONObject;
import org.marc4j.marc.Record;

public class AuthorityRecordDTO extends RecordDTO {
    private static final String[] AUTHOR_TYPE_TAGS = new String[] {"100", "110", "111"};

    @Serial private static final long serialVersionUID = 1L;

    private String authorName;
    private String authorOtherName;
    private String authorType;

    public String getAuthorName() {
        if (this.authorName == null) {
            MarcDataReader marcDataReader = new MarcDataReader(getRecord());

            this.authorName = marcDataReader.getAuthorName(true);
        }

        return this.authorName;
    }

    public String getAuthorType() {
        if (this.authorType == null) {
            Record record = getRecord();

            this.authorType =
                    Arrays.stream(AUTHOR_TYPE_TAGS)
                            .filter(tag -> record.getVariableField(tag) != null)
                            .findFirst()
                            .orElse("100");
        }

        return this.authorType;
    }

    public String getAuthorOtherName() {
        if (this.authorOtherName == null) {
            MarcDataReader marcDataReader = new MarcDataReader(getRecord());

            this.authorName = marcDataReader.getAuthorOtherName(true);
        }

        return this.authorOtherName;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = super.toJSONObject();

        json.putOpt("material_type", this.getMaterialType());
        json.putOpt("name", this.getAuthorName());
        json.putOpt("other_name", this.getAuthorOtherName());
        json.putOpt("author_type", this.getAuthorType());

        return json;
    }

    @Override
    protected void nullifyDerivedFields() {
        super.nullifyDerivedFields();

        authorName = null;
        authorOtherName = null;
    }
}
