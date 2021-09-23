package biblivre.core.configurations;

import java.util.List;

public interface ConfigurationsDAO {

    List<ConfigurationsDTO> list();

    boolean save(List<ConfigurationsDTO> configs, int loggedUser);
}
