package biblivre.marc;

import org.marc4j.marc.Record;

public class MarcDataReaderFactory {
	public static MarcDataReader getMarcDataReader(Record record) {
		return new MarcDataReaderImpl(record);
	}
}
