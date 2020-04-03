package biblivre.core.controllers;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import biblivre.core.schemas.SchemasDAO;

@org.springframework.stereotype.Controller
public class StatusController {

	@ResponseBody
	@GetMapping(path = "/status")
	protected String doGet() {
		JSONObject json;

		if (!SchemasDAO.getInstance("public").testDatabaseConnection()) {
			json = _generateFailureResponsePayload();
		} else {
			json = _generateSuccessResponsePayload();
		}

		return json.toString();
	}

	private JSONObject _generateSuccessResponsePayload() {
		JSONObject json = new JSONObject();

		json.put("success", true);

		json.put("status_message", "Disponível");

		return json;
	}

	private JSONObject _generateFailureResponsePayload() {
		JSONObject json = new JSONObject();

		json.put("success", false);

		json.put("status_message", "Falha no acesso ao Banco de Dados");

		return json;
	}
}
