package biblivre.core.update;

import java.sql.SQLException;

import biblivre.administration.indexing.IndexingBO;
import biblivre.cataloging.enums.RecordType;

public class UpdateBO_4_0_10_b extends UpdateBO {

	@Override
	protected void doSchemaUpdate(UpdatesDAO dao, String schema)
			throws SQLException {

		IndexingBO bo = IndexingBO.getInstance(schema);
		bo.reindex(RecordType.AUTHORITIES);
	}

	@Override
	protected String getVersion() {
		return "4.0.10b";
	}
}
