package biblivre.z3950.server;

import java.util.Collection;

public interface Z3950DAO {

    Collection<String> search(String value, Integer indexGroupId, int offset, int limit);
}
