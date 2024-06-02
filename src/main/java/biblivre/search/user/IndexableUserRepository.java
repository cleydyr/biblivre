package biblivre.search.user;

import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("elasticsearch")
public interface IndexableUserRepository extends ElasticsearchRepository<IndexableUser, String> {
    void deleteBySchemaAndTenant(String schema, String tenant);
}
