package biblivre.core.utils;

import org.json.JSONString;

public interface BiblivreEnum extends JSONString {
    @Override
    default String toJSONString() {
        return StringPool.DOUBLE_QUOTE + this + StringPool.DOUBLE_QUOTE;
    }
}
