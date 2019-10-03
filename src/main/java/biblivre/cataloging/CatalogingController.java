package biblivre.cataloging;

import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.core.ExtendedRequest;
import biblivre.core.auth.AuthorizationBO;
import biblivre.core.auth.AuthorizationPoints;

@RestController()
@RequestMapping("/cataloging.bibliographic")
public class CatalogingController {

	private AuthorizationBO _authorizationBO;

	public void setAuthorizationBO(AuthorizationBO authorizationBO) {
		_authorizationBO = authorizationBO;
	}

	@PostMapping(path = "database/{database}/item_count", produces=MediaType.APPLICATION_JSON_VALUE)
    public String itemCount(
    		@RequestAttribute String schema, @PathVariable String database,
    		@SessionAttribute AuthorizationPoints authPoints) {

		JSONObject responseBody = new JSONObject();

		RecordDatabase recordDatabase = RecordDatabase.fromString(database);

		if (recordDatabase == null) {
//			ValidationException ex = new ValidationException("cataloging.error.invalid_database");

			responseBody.put("success", false);

			return responseBody.toString();
		}
				
		if (recordDatabase == RecordDatabase.PRIVATE) {
			AuthorizationBO abo = AuthorizationBO.getInstance(schema);

			_authorizationBO.authorize(authPoints, "cataloging.bibliographic", "item_count");
		}		

		RecordBO bo = RecordBO.getInstance(schema, RecordType.BIBLIO);
		SearchQueryDTO query = new SearchQueryDTO(recordDatabase);
		SearchDTO dto = new SearchDTO(RecordType.BIBLIO);
		dto.setQuery(query);
		
		int count = bo.count(dto);
		
		try {
			responseBody.put("count", count);
			responseBody.put("success", true);
		} catch(JSONException e) {
//			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}

        return responseBody.toString();
    }

    private static final Logger _logger = LoggerFactory.getLogger(CatalogingController.class);
}
