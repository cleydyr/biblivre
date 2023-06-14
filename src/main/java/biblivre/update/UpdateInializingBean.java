package biblivre.update;

import biblivre.core.Updates;
import biblivre.core.schemas.SchemaDAO;
import biblivre.core.schemas.SchemaDTO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateInializingBean implements InitializingBean {
    private Updates updatesSuite;

    private SchemaDAO schemaDAO;

    @Override
    public void afterPropertiesSet() {
        updatesSuite.globalUpdate();

        for (SchemaDTO schema : schemaDAO.list()) {
            updatesSuite.schemaUpdate(schema.getSchema());
        }
    }

    @Autowired
    public void setUpdates(Updates updateSuite) {
        this.updatesSuite = updateSuite;
    }

    @Autowired
    public void setSchemaDAO(SchemaDAO schemaDAO) {
        this.schemaDAO = schemaDAO;
    }
}
