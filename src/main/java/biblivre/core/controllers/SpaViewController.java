package biblivre.core.controllers;

import biblivre.core.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaViewController {

    @GetMapping("/spa/**")
    public String spaPage(Model model, HttpServletRequest request) {
        String contextPath = request.getContextPath();
        model.addAttribute("contextPath", contextPath);
        model.addAttribute("contextPathJson", jsonQuoteForHtml(contextPath));
        model.addAttribute("spaStaticBase", SchemaUtil.buildSpaStaticBase(contextPath));

        String key = System.getenv(Constants.FLAGSMITH_ENVIRONMENT_KEY);
        if (key == null) {
            key = "";
        }
        model.addAttribute("flagsmithEnvironmentKeyJson", jsonQuoteForHtml(key));
        String apiUrl = System.getenv(Constants.FLAGSMITH_API_URL);
        if (apiUrl == null) {
            apiUrl = "";
        }
        model.addAttribute("flagsmithApiUrlJson", jsonQuoteForHtml(apiUrl));

        String viteDevServer = System.getenv(Constants.VITE_DEV_SERVER);
        if (StringUtils.isNotBlank(viteDevServer)) {
            model.addAttribute("viteDevServer", StringUtils.removeEnd(viteDevServer, "/"));
            return "spa-dev";
        }

        return "spa";
    }

    private static String jsonQuoteForHtml(String value) {
        return JSONObject.quote(value)
                .replace("<", "\\u003C")
                .replace(">", "\\u003E")
                .replace("&", "\\u0026");
    }
}
