package biblivre.core.persistence.sql;

import java.sql.Types;

public enum NullableSQLObject {
	DATE(Types.DATE),
	VARCHAR(Types.VARCHAR),
	FLOAT(Types.FLOAT),
	INTEGER(Types.INTEGER);

	private int type;

	private NullableSQLObject(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}
}
