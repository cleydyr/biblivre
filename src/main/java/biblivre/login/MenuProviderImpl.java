package biblivre.login;

import biblivre.core.properties.MenuPropertiesService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class MenuProviderImpl implements MenuProvider {
    @Value("classpath:META-INF/menus/menus.json")
    private Resource menusResource;

    private Map<Integer, JSONObject> prioritizedMenus;

    @Autowired private MenuPropertiesService menuPropertiesService;

    @Override
    public Map<String, List<String>> getAllowedModules(Predicate<String> filter) {
        if (prioritizedMenus == null) {
            prioritizedMenus = getPrioritizedMenus(readMenusFile());
        }

        Map<String, List<String>> allowedModules = new LinkedHashMap<>();

        prioritizedMenus
                .values()
                .forEach(
                        module -> {
                            JSONArray items = module.getJSONArray("items");

                            String name = module.getString("name");

                            items.forEach(
                                    obj -> {
                                        String item = obj.toString();

                                        if (filter.test(item)
                                                && menuPropertiesService.isEnabled(item)) {
                                            allowedModules
                                                    .computeIfAbsent(name, __ -> new ArrayList<>())
                                                    .add(item);
                                        }
                                    });
                        });

        return allowedModules;
    }

    private JSONObject readMenusFile() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            menusResource.getInputStream().transferTo(byteArrayOutputStream);
        } catch (IOException ioException) {
            logger.error("can't read menu files", ioException);
        }

        return new JSONObject(byteArrayOutputStream.toString());
    }

    private Map<Integer, JSONObject> getPrioritizedMenus(JSONObject json) {

        JSONArray modules = json.getJSONArray("modules");

        Map<Integer, JSONObject> prioritizedMenus = new TreeMap<>();

        modules.forEach(
                obj -> {
                    JSONObject module = (JSONObject) obj;

                    int priority = module.getInt("priority");

                    prioritizedMenus.put(priority, module);
                });

        return prioritizedMenus;
    }

    protected static final Logger logger = LoggerFactory.getLogger(MenuProviderImpl.class);
}
