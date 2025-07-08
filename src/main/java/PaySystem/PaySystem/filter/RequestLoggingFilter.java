package PaySystem.PaySystem.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*")  // Здесь указываем, что фильтр должен срабатывать для всех запросов
public class RequestLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация фильтра
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Преобразуем request к HttpServletRequest для удобства
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Логирование URL запроса
        System.out.println("Request URL: " + httpRequest.getRequestURI());
        System.out.println("Request Method: " + httpRequest.getMethod());

        // Передаем запрос дальше по цепочке
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Очистка ресурсов (если нужно)
    }
}
