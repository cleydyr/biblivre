package biblivre.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThymeleafController {

    @GetMapping("/view/thymeleaf-test")
    public String index(Model model) {
        model.addAttribute("message", "Thymeleaf is working correctly!");
        return "index";
    }
}
