package biblivre.core.controllers;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class PingController {

	@PostMapping(path = "/ping", produces=MediaType.APPLICATION_JSON_VALUE)
    public String ping() {
		JSONObject responseBody = new JSONObject();

        responseBody.put("success", true);

        _logger.debug("success!");

        return responseBody.toString();
    }

    private static final Logger _logger = LoggerFactory.getLogger(PingController.class);
}
