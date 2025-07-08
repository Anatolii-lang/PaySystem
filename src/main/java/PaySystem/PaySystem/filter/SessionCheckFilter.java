package PaySystem.PaySystem.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")  // Этот фильтр будет срабатывать на все запросы
public class SessionCheckFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация фильтра (если нужно)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(); // Получаем сессию

        // Пример проверки сессии
        Object user = session.getAttribute("user");

        if (user == null) {
            System.out.println("User is not logged in.");
        } else {
            System.out.println("User: " + user);
        }

        // Продолжаем выполнение цепочки фильтров (или передаем запрос дальше)
        chain.doFilter(request, response);
    }


}
