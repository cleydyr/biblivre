package biblivre.z3950.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k_int.sql.data_dictionary.DatabaseColAttribute;
import com.k_int.sql.data_dictionary.EntityKey;
import com.k_int.sql.data_dictionary.EntityTemplate;
import com.k_int.sql.data_dictionary.UnknownAccessPointException;

public class BiblivreEntityKey extends EntityKey {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> key_components = new HashMap<>();
	private Logger logger = LoggerFactory.getLogger(BiblivreEntityKey.class);

	public BiblivreEntityKey(String str) {
		String key_attr_name = "record";

		this.key_components.put(key_attr_name, str);
	}

	public BiblivreEntityKey(EntityTemplate et, ResultSet rs) {
		try {
			for (Iterator<?> e = et.getKeyAttrs(); e.hasNext();) {
				String key_attr_name = (String) e.next();

				DatabaseColAttribute dca = (DatabaseColAttribute) et.getAttributeDefinition(key_attr_name);

				int colpos = rs.findColumn(dca.getColName());

				Object o;

				switch (rs.getMetaData().getColumnType(colpos)) {
				case 2:
					o = new Integer(rs.getInt(colpos));
					break;
				case 12:
					try {
						o = new String(rs.getBytes(colpos), "UTF-8");
					} catch (Exception ex) {
						o = rs.getString(colpos);
					}
				default:
					o = rs.getObject(colpos);
				}

				this.key_components.put(key_attr_name, o);
			}
		} catch (SQLException sqle) {
			logger.error(sqle.getMessage(), sqle);
		} catch (NullPointerException npe) {
			logger.error(npe.getMessage(), npe);
		} catch (UnknownAccessPointException uape) {
			logger.error(uape.getMessage(), uape);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(numComponents() * 5 - 1);

		try {
			for (Iterator<String> e = getAttrNames(); e.hasNext();) {
				String attrname = e.next();

				Object attrval = this.key_components.get(attrname);

				sb.append(attrname)
					.append("='")
					.append(attrval.toString())
					.append("'");

				if (e.hasNext()) {
					sb.append(",");
				}
			}
		} catch (Exception e) {
			logger.warn("Unable to assemble key string", e);
		}

		return sb.toString();
	}

	public Map<String, Object> getKeyMap() {
		return this.key_components;
	}

	public void addKeyComponent(String attrname, Object attrval) {
		this.key_components.put(attrname, attrval);
	}

	public int numComponents() {
		return this.key_components.size();
	}

	public Iterator<String> getAttrNames() {
		return this.key_components.keySet().iterator();
	}

	public Object getAttrValue(String attrname) {
		return this.key_components.get(attrname);
	}
}
