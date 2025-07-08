package PaySystem.PaySystem.security;

import PaySystem.PaySystem.model.User;
import PaySystem.PaySystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Autowired
    public CustomAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setIsLoggedIn(true); // Устанавливаем флаг "пользователь в системе"
            userRepository.save(user);
            System.out.println("User " + username + " marked as logged in.");
        }

        // Проверка роли
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_admin")); // Именно так в Spring Security роль называется: ROLE_...

        if (isAdmin) {
            response.sendRedirect("/admin-page"); // Админ — на админку
        } else {
            response.sendRedirect("/home"); // Обычный пользователь — на домашнюю
        }
    }
}
