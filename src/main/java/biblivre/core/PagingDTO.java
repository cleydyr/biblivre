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

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

public final class PagingDTO implements IFJson {
    @Getter @Setter private long recordCount;
    @Getter @Setter private long recordOffset;
    @Setter @Getter private long recordsPerPage;
    @Setter @Getter private long recordLimit;

    @Setter @Getter private transient double time;
    private transient long startTime;
    private transient long endTime;

    public PagingDTO() {
        this.startTimer();
    }

    public PagingDTO(long recordCount, int recordsPerPage, int recordOffset) {
        this.startTimer();

        this.recordCount = recordCount;
        this.recordsPerPage = recordsPerPage;
        this.recordOffset = recordOffset;
    }

    public long getPage() {
        if (this.getRecordsPerPage() == 0) {
            return 1;
        }

        return (this.getRecordOffset() / this.getRecordsPerPage()) + 1;
    }

    public void setPage(int page) {
        this.recordOffset = (page - 1) * this.getRecordsPerPage();
    }

    public long getPageCount() {
        if (this.getRecordCount() == 0) {
            return 0;
        }

        if (this.getRecordsPerPage() == 0) {
            return 1;
        }

        double count = this.getRecordCount();

        if (this.getRecordLimit() > 0 && this.getRecordLimit() < count) {
            count = this.getRecordLimit();
        }

        return (int) Math.ceil(count / this.getRecordsPerPage());
    }

    public void setTime(long start, long end) {
        this.time = (end - start) / 1000.0;
    }

    public void startTimer() {
        this.startTime = new Date().getTime();
        this.setTime(this.startTime, this.endTime);
    }

    public void endTimer() {
        this.endTime = new Date().getTime();
        this.setTime(this.startTime, this.endTime);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        this.endTimer();

        json.putOpt("page", this.getPage());
        json.putOpt("page_count", this.getPageCount());
        json.putOpt("record_count", this.getRecordCount());
        json.putOpt("records_per_page", this.getRecordsPerPage());
        json.putOpt("record_limit", this.getRecordLimit());
        json.putOpt("time", this.getTime());

        return json;
    }
}
