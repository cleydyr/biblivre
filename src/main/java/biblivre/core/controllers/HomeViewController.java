package biblivre.core.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeViewController {

    @GetMapping("/view/home")
    public String examplePage(Model model) {
        model.addAttribute("title", "Example Page");
        model.addAttribute("message", "Welcome to the example page!");
        return "home";
    }
}
