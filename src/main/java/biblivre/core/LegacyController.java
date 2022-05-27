package biblivre.core;

import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/legacy")
public class LegacyController {
    @Autowired ServletContext servletContext;

    @GetMapping
    public void doGet() {
        System.out.println("get legacy!");
    }
}
