package biblivre.administration.accesscards;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.ExtendedRequest;
import biblivre.core.configurations.Configurations;
import biblivre.core.utils.Constants;

public class SearchParameters {
	public String code;
	public AccessCardStatus status;
	public int limit;
	public int offset;

	public SearchParameters(String code, AccessCardStatus status, int limit, int offset) {
		this.code = code;
		this.status = status;
		this.limit = limit;
		this.offset = offset;
	}

	public static SearchParameters extractSearchParameters(ExtendedRequest request) {
		String schema = request.getSchema();
		String searchParameters = request.getString("search_parameters");
		
		String query = null;
		AccessCardStatus status = null;
		try {
			JSONObject json = new JSONObject(searchParameters);
			query = json.optString("query");
			status = AccessCardStatus.fromString(json.optString("status"));
		} catch (JSONException je) {
			return null;
		}
		
		Integer limit = request.getInteger("limit", Configurations.getInt(schema, Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
		Integer offset = (request.getInteger("page", 1) - 1) * limit;

		SearchParameters parameterObject = new SearchParameters(query, status, limit, offset);

		return parameterObject;
	}
}