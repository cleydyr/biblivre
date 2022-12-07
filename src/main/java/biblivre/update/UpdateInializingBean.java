package biblivre.update;

import biblivre.core.Updates;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateInializingBean implements InitializingBean {
    private Updates updatesSuite;

    @Override
    public void afterPropertiesSet() throws Exception {
        updatesSuite.globalUpdate();
    }

    @Autowired
    public void setUpdates(Updates updateSuite) {
        this.updatesSuite = updateSuite;
    }
}
