package biblivre.login;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface MenuProvider {
    Map<String, List<String>> getAllowedModules(Predicate<String> filter);
}
