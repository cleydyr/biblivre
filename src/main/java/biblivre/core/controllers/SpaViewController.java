package biblivre.core.controllers;

import biblivre.core.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaViewController {

    private final ObjectMapper objectMapper;

    public SpaViewController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/spa/**")
    public String spaPage(Model model) throws JsonProcessingException {
        String key = System.getenv(Constants.FLAGSMITH_ENVIRONMENT_KEY);
        if (key == null) {
            key = "";
        }
        model.addAttribute("flagsmithEnvironmentKeyJson", objectMapper.writeValueAsString(key));
        return "spa";
    }
}
