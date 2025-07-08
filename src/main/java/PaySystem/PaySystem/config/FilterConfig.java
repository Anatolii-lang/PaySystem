package PaySystem.PaySystem.config;

import PaySystem.PaySystem.filter.SessionCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<SessionCheckFilter> sessionCheckFilter() {
        FilterRegistrationBean<SessionCheckFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SessionCheckFilter());  // Регистрируем фильтр
        registrationBean.addUrlPatterns("/"); // Этот фильтр будет срабатывать на все URL
        return registrationBean;
    }
}
