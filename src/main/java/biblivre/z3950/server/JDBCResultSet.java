package biblivre.z3950.server;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.jzkit.search.provider.jdbc.JDBCSearchable;
import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.RecordModel.RecordFormatSpecification;
import org.jzkit.search.util.RecordModel.iso2709;
import org.jzkit.search.util.ResultSet.AbstractIRResultSet;
import org.jzkit.search.util.ResultSet.IFSNotificationTarget;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.jzkit.search.util.ResultSet.IRResultSetException;
import org.jzkit.search.util.ResultSet.IRResultSetInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k_int.sql.data_dictionary.OID;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.LongComparator;

public class JDBCResultSet extends AbstractIRResultSet implements IRResultSet {
	private static Logger log = LoggerFactory.getLogger(AbstractIRResultSet.class);
	int num_hits = 0;
	private RecordManager recman = null;
	private BTree tree = null;
	private String results_file_name;
	private static long instance_counter = 0L;

	public JDBCResultSet(JDBCSearchable owner) {
		log.info("New JDBCResultSet:" + ++instance_counter);
	}

	protected void finalize() {
		log.info("JDBCResultSet::finalize" + --instance_counter);
	}

	public void init() {
		try {
			File results_file = null;
			String dir = System.getProperty("com.k_int.inode.tmpdir");
			if (dir != null) {
				results_file = File.createTempFile("JDBCRS", "jdbm", new File(dir + "/jdbc"));
			} else {
				results_file = File.createTempFile("JDBCRS", "jdbm");
			}
			this.results_file_name = results_file.toString();
			Properties props = new Properties();
			props.put("jdbm.cache.size", "500");
			props.put("jdbm.disableTransactions", "true");
			this.recman = RecordManagerFactory.createRecordManager(this.results_file_name, props);
			results_file.delete();
			this.tree = BTree.createInstance(this.recman, new LongComparator());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public InformationFragment[] getFragment(int starting_fragment, int count, RecordFormatSpecification spec)
			throws IRResultSetException {
		InformationFragment[] result = new InformationFragment[count];
		try {
			for (int i = 0; i < count; i++) {
				long recno = starting_fragment - 1 + i;
				OID oid = (OID) this.tree.find(new Long(recno));
				String record = (String) oid.getKeyPairs().getAttrValue("record");
				result[i] = new iso2709(record.getBytes("UTF-8"), "UTF-8");
				result[i].setHitNo(recno + 1L);
			}
		} catch (IOException ioe) {
			throw new IRResultSetException("Problem retrieving record", ioe);
		}
		return result;
	}

	public void asyncGetFragment(int starting_fragment, int count, RecordFormatSpecification spec,
			IFSNotificationTarget target) {
		try {
			InformationFragment[] result = getFragment(starting_fragment, count, spec);
			target.notifyRecords(result);
		} catch (IRResultSetException re) {
			target.notifyError("JDBC", new Integer(0), "No reason", re);
		}
	}

	public int getFragmentCount() {
		return this.num_hits;
	}

	public int getRecordAvailableHWM() {
		return this.num_hits;
	}

	public void close() {
		log.info("JDBCResultSet::close() ");
		try {
			this.recman.close();
			log.info("Deleting JDBC Results " + this.results_file_name + "[.db,.lg]");
			File f = new File(this.results_file_name + ".db");
			f.delete();
			f = null;
			f = new File(this.results_file_name + ".lg");
			f.delete();
			f = null;
		} catch (IOException ioe) {
			log.warn("Problem deleting temp files", ioe);
			ioe.printStackTrace();
		}
	}

	public void add(OID key) {
		try {
			this.tree.insert(new Long(this.num_hits++), key, false);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	protected void commit() {
		try {
			this.recman.commit();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public IRResultSetInfo getResultSetInfo() {
		return new IRResultSetInfo(getResultSetName(), "JDBC", null, getFragmentCount(), getStatus(), null);
	}
}
