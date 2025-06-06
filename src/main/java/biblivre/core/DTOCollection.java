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
package biblivre.core;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.JSONObject;

public class DTOCollection<T extends AbstractDTO> extends AbstractCollection<T> implements IFJson {
    private static final DTOCollection<? extends AbstractDTO> EMPTY = new DTOCollection<>();

    @Setter @Getter private Integer id;

    @Setter @Getter private PagingDTO paging;

    private final List<T> list = new ArrayList<>();

    public long getRecordLimit() {
        if (this.paging == null) {
            return 0;
        }
        return this.paging.getRecordLimit();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json;

        if (this.getPaging() != null) {
            json = this.getPaging().toJSONObject();
        } else {
            json = new JSONObject();
        }

        json.putOpt("id", this.getId());
        for (T dto : this) {
            json.append("data", dto.toJSONObject());
        }

        return json;
    }

    @Override
    @NonNull
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean add(T e) {
        return list.add(e);
    }

    public static DTOCollection<? extends AbstractDTO> empty() {
        return EMPTY;
    }
}
