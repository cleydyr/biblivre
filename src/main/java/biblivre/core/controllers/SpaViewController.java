package biblivre.core.controllers;

import biblivre.core.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaViewController {

    @GetMapping("/spa/**")
    public String spaPage(Model model) {
        String key = System.getenv(Constants.FLAGSMITH_ENVIRONMENT_KEY);
        if (key == null) {
            key = "";
        }
        model.addAttribute(
                "flagsmithEnvironmentKeyJson",
                JSONObject.quote(key)
                        .replace("<", "\\u003C")
                        .replace(">", "\\u003E")
                        .replace("&", "\\u0026"));
        String apiUrl = System.getenv(Constants.FLAGSMITH_API_URL);
        if (apiUrl == null) {
            apiUrl = "";
        }
        model.addAttribute(
                "flagsmithApiUrlJson",
                JSONObject.quote(apiUrl)
                        .replace("<", "\\u003C")
                        .replace(">", "\\u003E")
                        .replace("&", "\\u0026"));

        String viteDevServer = System.getenv(Constants.VITE_DEV_SERVER);
        if (StringUtils.isNotBlank(viteDevServer)) {
            model.addAttribute("viteDevServer", StringUtils.removeEnd(viteDevServer, "/"));
            return "spa-dev";
        }

        return "spa";
    }
}
