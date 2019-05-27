package biblivre.core.utils;

import org.json.JSONString;

public interface JSONStringEnum extends JSONString {
	public String name();

	@Override
	public default String toJSONString() {
		return StringPool.DOUBLE_QUOTE + this.name().toLowerCase() + StringPool.DOUBLE_QUOTE;
	}
}
