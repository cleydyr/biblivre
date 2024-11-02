package biblivre.circulation.user;

import biblivre.circulation.lending.LendingDAO;
import biblivre.circulation.lending.LendingFineDAO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.schemas.SchemaBO;
import biblivre.search.Reindexable;
import biblivre.search.SearchException;
import biblivre.search.user.IndexableUser;
import biblivre.search.user.IndexableUserQueryParameters;
import biblivre.search.user.IndexableUserRepository;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ObjectBuilder;
import jakarta.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@Profile("elasticsearch")
@Primary
@Slf4j
public class IndexableUserDAO extends UserDAOImpl implements Reindexable<UserDTO> {
    @Autowired private IndexableUserRepository indexableUserRepository;

    @Value(value = "${biblivre.cloud.tentant:localhost}")
    private String tenant;

    @Autowired private LendingFineDAO lendingFineDAO;

    @Autowired private LendingDAO lendingDAO;

    @Autowired private ElasticsearchOperations elasticsearchOperations;

    @Autowired private SchemaBO schemaBO;

    @Override
    public @Nonnull DTOCollection<UserDTO> search(UserSearchDTO dto, int limit, int offset)
            throws SearchException {
        List<Query> filterQueries = getFilterQueries(dto);

        List<Query> mustQueries = getMustQueries(dto);

        NativeQuery nativeQuery =
                NativeQuery.builder()
                        .withQuery(
                                query ->
                                        query.bool(
                                                bool ->
                                                        bool.filter(filterQueries)
                                                                .must(mustQueries)))
                        .withPageable(PageRequest.of(offset / limit, limit))
                        .build();

        SearchHits<IndexableUser> searchHits =
                elasticsearchOperations.search(nativeQuery, IndexableUser.class);

        DTOCollection<UserDTO> result =
                searchHits.getSearchHits().stream()
                        .map(hit -> getUserDTO(hit.getContent()))
                        .collect(DTOCollection::new, DTOCollection::add, DTOCollection::addAll);

        result.setPaging(new PagingDTO(searchHits.getTotalHits(), limit, offset));

        return result;
    }

    @Override
    public void reindexAllSchemas() throws SearchException {
        for (var schema : schemaBO.getSchemas()) {
            log.info("Reindexing schema: {}", schema.getSchema());

            SchemaThreadLocal.withSchema(schema.getSchema(), () -> reindex(schema.getSchema()));
        }
    }

    @Override
    public void reindex(String schema) throws SearchException {
        indexableUserRepository.deleteBySchemaAndTenant(schema, tenant);

        Collection<UserDTO> allUsers = super.listAllUsers();

        bulkIndex(allUsers.stream().map(this::getIndexableUser).toList());
    }

    private List<Query> getFilterQueries(UserSearchDTO dto) {
        var baseFilterTermQueries = buildBaseQueries(dto);

        var textQueries = buildTextQueries(dto);

        var advancedOptionsQueries = buildAdvancedOptionsQueries(dto);

        var filterTermQueries = new ArrayList<>(baseFilterTermQueries);

        filterTermQueries.addAll(textQueries);

        filterTermQueries.addAll(advancedOptionsQueries);

        return Collections.unmodifiableList(filterTermQueries);
    }

    private List<Query> buildTextQueries(UserSearchDTO dto) {
        if (dto.isSearchById()) {
            return Collections.singletonList(buildUserIdTermQuery(dto));
        }

        if (dto.getField().isBlank()) {
            return Collections.emptyList();
        }

        return buildWildcardQuery(dto);
    }

    private static List<Query> buildWildcardQuery(UserSearchDTO dto) {
        String query = "*%s*".formatted(dto.getQuery());

        String fieldToSearch = "fields.%s".formatted(dto.getField());

        return List.of(
                new Query.Builder()
                        .queryString(queryString -> queryString.query(query).fields(fieldToSearch))
                        .build());
    }

    private List<Query> buildBaseQueries(UserSearchDTO dto) {
        Query schemaQ = buildStringTermQuery("schema", SchemaThreadLocal.get());

        Query tenantQ = buildStringTermQuery("tenant", tenant);

        return List.of(schemaQ, tenantQ);
    }

    private static List<Query> buildAdvancedOptionsQueries(UserSearchDTO dto) {
        if (dto.isSimpleSearch()) {
            return Collections.emptyList();
        }

        List<Query> filterQueries = new ArrayList<>();

        if (dto.isUserCardNeverPrinted()) {
            filterQueries.add(buildBooleanTermQuery("userCardPrinted", false));
        }

        if (dto.isPendingFines()) {
            filterQueries.add(buildBooleanTermQuery("hasPendingFines", true));
        }

        if (dto.isInactiveOnly()) {
            filterQueries.add(buildBooleanTermQuery("isInactive", true));
        }

        if (dto.isLateLendings()) {
            filterQueries.add(buildBooleanTermQuery("hasPendingLoans", true));
        }

        if (dto.isLoginAccess()) {
            filterQueries.add(buildBooleanTermQuery("hasLogin", true));
        }

        if (dto.getCreatedStartDate() != null) {
            filterQueries.add(buildRangeGteQuery("created", dto.getCreatedStartDate()));
        }

        if (dto.getCreatedEndDate() != null) {
            filterQueries.add(buildRangeLteQuery("created", dto.getCreatedEndDate()));
        }

        if (dto.getModifiedStartDate() != null) {
            filterQueries.add(buildRangeGteQuery("modified", dto.getModifiedStartDate()));
        }

        if (dto.getModifiedEndDate() != null) {
            filterQueries.add(buildRangeLteQuery("modified", dto.getModifiedEndDate()));
        }

        return Collections.unmodifiableList(filterQueries);
    }

    private static List<Query> getMustQueries(UserSearchDTO dto) {
        if (dto.isSearchById() || dto.isListAll()) {
            return Collections.emptyList();
        }

        return List.of(getMustQueryForName(dto));
    }

    private static Query getMustQueryForName(UserSearchDTO dto) {
        return new Query.Builder()
                .match(match -> match.field("name").query(dto.getQuery()).fuzziness("AUTO"))
                .build();
    }

    private static Query buildUserIdTermQuery(UserSearchDTO dto) {
        return buildTermQuery("userId", value -> value.longValue(Long.parseLong(dto.getQuery())));
    }

    private static Query buildRangeGteQuery(String field, Date value) {
        return new Query.Builder()
                .range(range -> range.field(field).gte(JsonData.of(value)))
                .build();
    }

    private static Query buildRangeLteQuery(String field, Date value) {
        return new Query.Builder()
                .range(range -> range.field(field).lte(JsonData.of(value)))
                .build();
    }

    private static Query buildBooleanTermQuery(String field, boolean value) {
        return buildTermQuery(field, v -> v.booleanValue(value));
    }

    private static Query buildStringTermQuery(String field, String value) {
        return buildTermQuery(field, v -> v.stringValue(value));
    }

    private static Query buildTermQuery(
            String field, Function<FieldValue.Builder, ObjectBuilder<FieldValue>> valueFunction) {
        return new Query.Builder().term(term -> term.field(field).value(valueFunction)).build();
    }

    private IndexableUserQueryParameters getIndexableUserQueryParameters(UserSearchDTO userSearch) {
        return new IndexableUserQueryParameters(
                parseInt(userSearch.getQuery()).orElse(-1),
                userSearch.getQuery(),
                SchemaThreadLocal.get(),
                tenant,
                userSearch.isLoginAccess(),
                userSearch.isPendingFines(),
                userSearch.isLateLendings(),
                userSearch.isUserCardNeverPrinted(),
                userSearch.isInactiveOnly());
    }

    @Override
    public UserDTO save(UserDTO user) throws SearchException {
        UserDTO saved = super.save(user);

        index(user);

        return saved;
    }

    private void index(UserDTO user) throws SearchException {
        indexableUserRepository.save(getIndexableUser(user));
    }

    @Override
    public boolean delete(UserDTO user) throws SearchException {
        super.delete(user);

        indexableUserRepository.delete(getIndexableUser(user));

        return true;
    }

    @Override
    public void markAsPrinted(Collection<Integer> ids) throws SearchException {
        super.markAsPrinted(ids);

        reindex(ids);
    }

    @Override
    public boolean updateUserStatus(Integer userId, UserStatus status) throws SearchException {
        super.updateUserStatus(userId, status);

        reindex(Set.of(userId));

        return true;
    }

    private void reindex(Collection<Integer> ids) throws SearchException {
        Map<Integer, UserDTO> usersMap = super.map(ids);

        bulkIndex(usersMap.values().stream().map(this::getIndexableUser).toList());
    }

    private void bulkIndex(Collection<IndexableUser> users) {
        indexableUserRepository.saveAll(users);
    }

    private IndexableUser getIndexableUser(UserDTO user) {
        int userId = user.getId();

        boolean userHasPendingFines = lendingFineDAO.hasPendingFine(userId);

        boolean userHasPendingLoans = lendingDAO.hasLateLendings(userId);

        String schema = SchemaThreadLocal.get();

        return new IndexableUser(
                tenant + ":" + schema + ":" + userId,
                userId,
                user.getName(),
                user.getType(),
                user.getPhotoId(),
                user.getStatus().toString(),
                user.getLoginId() == null ? -1 : user.getLoginId(),
                user.getCreated(),
                user.getCreatedBy(),
                user.getModified(),
                user.getModifiedBy(),
                user.isUserCardPrinted(),
                user.getFields(),
                userHasPendingFines,
                userHasPendingLoans,
                user.hasLogin(),
                user.isInactive(),
                schema,
                tenant);
    }

    private static UserDTO getUserDTO(IndexableUser indexableUser) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(indexableUser.getUserId());
        userDTO.setName(indexableUser.getName());
        userDTO.setType(indexableUser.getType());
        userDTO.setPhotoId(indexableUser.getPhotoId());
        userDTO.setStatus(UserStatus.fromString(indexableUser.getStatus()));
        userDTO.setLoginId(indexableUser.getLoginId() == -1 ? null : indexableUser.getLoginId());
        userDTO.setCreatedBy(indexableUser.getCreatedBy());
        userDTO.setCreated(indexableUser.getCreated());
        userDTO.setModifiedBy(indexableUser.getModifiedBy());
        userDTO.setModified(indexableUser.getModified());
        userDTO.setUserCardPrinted(indexableUser.isUserCardPrinted());
        userDTO.setFields(indexableUser.getFields());

        return userDTO;
    }

    private static OptionalInt parseInt(String query) {
        try {
            return OptionalInt.of(Integer.parseInt(query));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}
