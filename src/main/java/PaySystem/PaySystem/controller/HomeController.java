package PaySystem.PaySystem.controller;  // Пакет контроллера, убедись, что он правильный

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")  // Это путь главной страницы
    public String index() {
        return "index";  // Thymeleaf будет искать файл "index.html" в папке "templates"
    }


}
