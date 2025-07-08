package PaySystem.PaySystem.security;

import PaySystem.PaySystem.model.User;
import PaySystem.PaySystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final UserRepository userRepository;

    @Autowired
    public CustomLogoutSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        if (authentication != null) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElse(null);

            if (user != null) {
                System.out.println("User " + username + " logged out. Updating 'is_logged_in'...");
                user.setIsLoggedIn(false); // ← устанавливаем поле is_logged_in в false
                userRepository.save(user);
                System.out.println("User " + username + " marked as logged out.");
            }
        } else {
            System.out.println("No authentication found during logout.");
        }

        response.sendRedirect("/login");
    }

}
