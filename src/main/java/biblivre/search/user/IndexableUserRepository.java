package biblivre.search.user;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IndexableUserRepository extends ElasticsearchRepository<IndexableUser, Integer> {
    void deleteBySchemaAndTenant(String schema, String tenant);
}
