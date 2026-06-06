package biblivre.core.controllers;

import biblivre.core.utils.Constants;
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
        model.addAttribute("flagsmithEnvironmentKeyJson", JSONObject.quote(key));
        String apiUrl = System.getenv(Constants.FLAGSMITH_API_URL);
        if (apiUrl == null) {
            apiUrl = "";
        }
        model.addAttribute("flagsmithApiUrlJson", JSONObject.quote(apiUrl));
        
        return "spa";
    }
}
