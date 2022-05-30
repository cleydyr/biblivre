package biblivre.controller;

import biblivre.cataloging.Fields;
import biblivre.circulation.user.UserFields;
import biblivre.core.IFCacheableJavascript;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.translations.Translations;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class StaticController {

    @GetMapping("/static/scripts/{fileName}")
    public String getScripts(@PathVariable String fileName) {
        if (fileName.endsWith(".i18n.js")
                || fileName.endsWith(".form.js")
                || fileName.endsWith(".user_fields.js")) {

            String[] params = StringUtils.split(fileName, ".");

            String schema = params[0];

            IFCacheableJavascript javascript = null;

            if (fileName.endsWith(".i18n.js")) {
                javascript = Translations.get(params[1]);
            } else if (fileName.endsWith(".user_fields.js")) {
                javascript = SchemaThreadLocal.withSchema(schema, UserFields::getFields);
            } else {
                javascript =
                        SchemaThreadLocal.withSchema(schema, () -> Fields.getFormFields(params[2]));
            }

            return javascript.toJavascriptString();
        }

        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
    }
}
