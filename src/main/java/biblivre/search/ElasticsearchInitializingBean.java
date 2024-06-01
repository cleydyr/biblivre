package biblivre.search;

import biblivre.circulation.user.IndexableUserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("elasticsearch")
@Slf4j
public class ElasticsearchInitializingBean implements InitializingBean {
    @Value("${biblivre.startup.action.elasticsearch.reindex:false}")
    private boolean reindexOnStartup;

    @Autowired private IndexableUserDAO indexableUserDAO;

    @Override
    public void afterPropertiesSet() throws SearchException {
        log.info("ElasticsearchInitializingBean afterPropertiesSet");

        if (reindexOnStartup) {
            log.info("Reindexing all schemas on startup");

            indexableUserDAO.reindexAllSchemas();
        }
    }
}
