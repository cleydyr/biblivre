package biblivre.core;

import biblivre.core.file.BiblivreFile;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Setter
@Getter
public class HandlerContext {
    private JSONObject json;
    private String jspURL;
    private Message message;
    private BiblivreFile file;
    private int returnCode;
    private HttpCallback callback;

    public HandlerContext() {
        json = new JSONObject();

        message = new Message();
    }
}
