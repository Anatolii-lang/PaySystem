package PaySystem.PaySystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class BaseController {

    // Обработчик для главной страницы
    @GetMapping("/home")
    public String home(Principal principal, Model model) {
        // Проверяем, залогинен ли пользователь
        if (principal != null) {
            // Если залогинен, добавляем имя пользователя в модель
            model.addAttribute("username", principal.getName());
        } else {
            // Если не залогинен, не добавляем имя
            model.addAttribute("username", null);
        }
        return "home";  // имя шаблона (home.html)
    }


}
