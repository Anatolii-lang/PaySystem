package PaySystem.PaySystem.service;

import PaySystem.PaySystem.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class CustomUserDetails implements UserDetails {
    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())); // Преобразуем роль в authority
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Можно добавить свою логику проверки
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Можно добавить свою логику проверки
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Можно добавить свою логику проверки
    }

    @Override
    public boolean isEnabled() {
        return user.isActive(); // Если пользователь активен, то аккаунт включен
    }

    // Можно добавить дополнительные методы для получения данных пользователя
    public User getUser() {
        return user;
    }
}
