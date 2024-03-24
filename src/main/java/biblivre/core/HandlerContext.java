package biblivre.core;

import biblivre.core.file.BiblivreFile;
import org.json.JSONObject;

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

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getJspURL() {
        return jspURL;
    }

    public void setJspURL(String jspURL) {
        this.jspURL = jspURL;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public BiblivreFile getFile() {
        return file;
    }

    public void setFile(BiblivreFile file) {
        this.file = file;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public HttpCallback getCallback() {
        return callback;
    }

    public void setCallback(HttpCallback callback) {
        this.callback = callback;
    }
}
