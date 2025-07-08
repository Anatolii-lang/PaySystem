package PaySystem.PaySystem.controller;

import PaySystem.PaySystem.service.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/session") // базовый путь для этого контроллера
public class SessionController {

    // Сохранение данных в сессию
    @GetMapping("/save-user")
    public String saveUserInSession(HttpSession session) {
        // Например, сохраняем имя пользователя
        session.setAttribute("username", "Ivan Ivanov");
        return "redirect:/session/show-user"; // После сохранения перенаправляем на отображение сессии
    }

    // Отображение данных в сессии
    @GetMapping("/show-user")
    @ResponseBody
    public String showSession(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails != null) {
            return "<h2>Данные пользователя:</h2><p>Имя пользователя: " + customUserDetails.getUsername() + "</p>";
        } else {
            return "<h2>Пользователь не найден в сессии!</h2>";
        }
    }



    // Очистка сессии
    @GetMapping("/clear-session")
    public String clearSession(HttpSession session) {
        session.invalidate(); // Полностью очищаем сессию
        return "redirect:/login"; // Перенаправляем на страницу входа после очистки
    }
}
