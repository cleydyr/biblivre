package biblivre.core;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/legacy")
public class LegacyController {

    @GetMapping
    public void doGet() {
        System.out.println("get legacy!");
    }
}
