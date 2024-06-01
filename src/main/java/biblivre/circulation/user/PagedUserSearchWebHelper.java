package biblivre.circulation.user;

import biblivre.core.ExtendedRequest;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagedUserSearchWebHelper {
    @Autowired private ConfigurationBO configurationBO;

    public PagedUserSearchDTO getPagedUserSearchDTO(ExtendedRequest request) {
        String searchParameters = request.getString("search_parameters");

        UserSearchDTO searchDto = new UserSearchDTO(searchParameters);

        Integer limit =
                request.getInteger(
                        "limit", configurationBO.getInt(Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));

        Integer offset = request.getInteger("offset", 0);

        Integer page = request.getInteger("page", 1);

        if (page > 1) {
            offset = limit * (page - 1);
        }

        return new PagedUserSearchDTO(searchDto, limit, offset);
    }
}
