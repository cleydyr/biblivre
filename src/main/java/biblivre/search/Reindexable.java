package biblivre.search;

public interface Reindexable<T> {
    void reindexAllSchemas() throws SearchException;

    void reindex(String schema) throws SearchException;
}
