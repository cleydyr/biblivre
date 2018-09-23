package biblivre.core.update;

import java.sql.SQLException;

import biblivre.administration.indexing.IndexingBO;
import biblivre.cataloging.enums.RecordType;

public class UpdateBO_4_1_6 extends UpdateBO {

	@Override
	protected void doSchemaUpdate(UpdatesDAO dao, String schema) throws SQLException {
		IndexingBO bo = IndexingBO.getInstance(schema);

		bo.reindex(RecordType.BIBLIO);

		bo.reindex(RecordType.AUTHORITIES);

		bo.reindex(RecordType.VOCABULARY);
	}

	@Override
	protected String getVersion() {
		return "4.1.6";
	}
}
