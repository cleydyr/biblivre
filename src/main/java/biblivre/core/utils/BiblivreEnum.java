package biblivre.core.utils;

import org.json.JSONString;

public interface BiblivreEnum extends JSONString {
	@Override
	public default String toJSONString() {
		return StringPool.DOUBLE_QUOTE + toString() + StringPool.DOUBLE_QUOTE;
	}
}
