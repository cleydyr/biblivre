package biblivre.administration.customization.v2;

import biblivre.generated.api.model.RestRecordType;
import org.springframework.core.convert.converter.Converter;

public class StringToRecordTypeConverter implements Converter<String, RestRecordType> {

    @Override
    public RestRecordType convert(String source) {
        return RestRecordType.valueOf(source.toUpperCase());
    }
}
