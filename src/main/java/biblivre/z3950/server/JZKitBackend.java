
/**
 *  Este arquivo é parte do Biblivre 5.
 *
 *  Biblivre 5 é um software livre; você pode redistribuí-lo e/ou
 *  modificá-lo dentro dos termos da Licença Pública Geral GNU como
 *  publicada pela Fundação do Software Livre (FSF); na versão 3 da
 *  Licença, ou (caso queira) qualquer versão posterior.
 *
 *  Este programa é distribuído na esperança de que possa ser  útil,
 *  mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  Licença Pública Geral GNU para maiores detalhes.
 *
 *  Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 *  @author Alberto Wagner <alberto@biblivre.org.br>
 *  @author Danniel Willian <danniel@biblivre.org.br>
 *
 */


package biblivre.z3950.server;

import java.util.HashMap;
import java.util.Map;
import org.jzkit.search.LandscapeSpecification;
import org.jzkit.search.SearchSession;
import org.jzkit.search.landscape.SimpleLandscapeSpecification;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.RecordModel.ArchetypeRecordFormatSpecification;
import org.jzkit.search.util.RecordModel.RecordFormatSpecification;
import org.jzkit.search.util.ResultSet.IRResultSetStatus;
import org.jzkit.search.util.ResultSet.RSStatusMaskCondition;
import org.jzkit.search.util.ResultSet.TransformingIRResultSet;
import org.jzkit.z3950.QueryModel.Type1QueryModel;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.RPNQuery_type;
import org.jzkit.z3950.server.BackendPresentResult;
import org.jzkit.z3950.server.BackendSearchResult;
import org.jzkit.z3950.server.Z3950NonBlockingBackend;
import org.jzkit.z3950.server.ZServerAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class JZKitBackend implements Z3950NonBlockingBackend, ApplicationContextAware {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private ApplicationContext ctx = null;
    private transient SearchSession search_session = null;
    private Map<String, TransformingIRResultSet> result_sets = new HashMap<>();
    private static final RecordFormatSpecification SPEC = new ArchetypeRecordFormatSpecification("F");

    public JZKitBackend() {}

    @Override
    public void search(BackendSearchResult bsr) {
        checkSearchSession();

        QueryModel qm = new Type1QueryModel((RPNQuery_type) (bsr.query.o));

        LandscapeSpecification ls = new SimpleLandscapeSpecification(bsr.database_names);

        try {
            TransformingIRResultSet active_result_set =
                    search_session.search(ls, qm, null, null, SPEC);
            bsr.search_status = true;
            bsr.result_count = 0;
            if ((bsr.result_set_name != null) && (bsr.result_set_name.length() > 0)) {
                result_sets.put(bsr.result_set_name, active_result_set);
            } else {
                result_sets.put("default", active_result_set);
            }
            if (active_result_set.waitForCondition(new RSStatusMaskCondition(IRResultSetStatus.COMPLETE | IRResultSetStatus.FAILURE), 10000)) {
                bsr.result_count = active_result_set.getFragmentCount();
            } else {
                // timeout...
                bsr.search_status = false;
            }
        } catch (org.jzkit.search.util.ResultSet.IRResultSetException irrse) {
            log.error(irrse.getMessage(), irrse);
            bsr.search_status = false;
        } catch (org.jzkit.search.SearchException se) {
            log.error(se.getMessage(), se);
            bsr.search_status = false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            bsr.search_status = false;
        }
        bsr.assoc.notifySearchResult(bsr);
    }

    /**
     * Implementor must call assoc.notifyPresentResult(result);
     */
    @Override
    public void present(BackendPresentResult bpr) {
        try {
            TransformingIRResultSet rs = (TransformingIRResultSet) result_sets.get(bpr.result_set_name);
            if (rs != null) {
                bpr.result_records = rs.getFragment(bpr.start,
                        bpr.count,
                        bpr.archetype);
                bpr.next_result_set_position = bpr.start + bpr.count;
            }
        } catch (org.jzkit.search.util.ResultSet.IRResultSetException irrse) {
            irrse.printStackTrace();
        }
        bpr.assoc.notifyPresentResult(bpr);
    }



    /**
     * Implementor must call assoc.notifyDeleteResultSetResult(result);
     */
    @Override
    public void deleteResultSet(ZServerAssociation assoc,
            String result_set_name, Object refid) {}

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    private synchronized void checkSearchSession() {
        search_session = (org.jzkit.search.impl.SearchSessionImpl) ctx.getBean("SearchService");
    }

}
