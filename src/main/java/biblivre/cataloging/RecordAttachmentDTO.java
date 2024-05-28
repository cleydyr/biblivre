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

import biblivre.core.AbstractDTO;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

@Setter
@Getter
@AllArgsConstructor
public class RecordAttachmentDTO extends AbstractDTO {
    @Serial private static final long serialVersionUID = 1L;

    private String file;
    private String name;
    private String path;
    private String uri;

    public void normalizeFileAndName() {
        if (StringUtils.isBlank(file)) {
            file = name;
        }

        if (StringUtils.isBlank(file)) {
            file = uri;
        }

        if (StringUtils.isBlank(name)) {
            name = file;
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        json.putOpt("path", this.getPath());
        json.putOpt("file", this.getFile());
        json.putOpt("name", this.getName());
        json.putOpt("uri", this.getUri());

        return json;
    }
}
