package biblivre.search;

import biblivre.circulation.user.IndexableUserDAO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("elasticsearch")
public class ElasticsearchInitializingBean implements InitializingBean {
    @Value("${elasticsearch.startup.action.reindex:false}")
    private boolean reindexOnStartup;

    @Autowired private IndexableUserDAO indexableUserDAO;

    @Override
    public void afterPropertiesSet() throws SearchException {
        if (reindexOnStartup) {
            indexableUserDAO.reindexAll();
        }
    }
}
