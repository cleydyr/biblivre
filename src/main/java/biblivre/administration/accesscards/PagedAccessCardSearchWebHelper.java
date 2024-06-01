package biblivre.administration.accesscards;

import biblivre.core.ExtendedRequest;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.utils.Constants;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagedAccessCardSearchWebHelper {
    @Autowired private ConfigurationBO configurationBO;

    public PagedAccessCardSearchDTO getPagedAccessCardSearchDTO(ExtendedRequest request) {
        String searchParameters = request.getString("search_parameters");

        JSONObject json = new JSONObject(searchParameters);
        String query = json.optString("query");
        var status = AccessCardStatus.fromString(json.optString("status"));

        Integer limit =
                request.getInteger(
                        "limit", configurationBO.getInt(Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
        int offset = (request.getInteger("page", 1) - 1) * limit;

        return new PagedAccessCardSearchDTO(query, status, limit, offset);
    }
}
