package pl.com.tt.ttime.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import pl.com.tt.ttime.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected void configure(HttpSecurity http) throws Exception {
        http
                //@formatter:off
                .httpBasic()
                .and()
                .authorizeRequests()
                    .antMatchers("/css/**", "/", "/index.html", "/login.html",
                            "/user", "/js/**", "/fullcalendar/**", "/fonts/**", "/api/**", "/favicon.ico").permitAll()
//                    .requestMatchers(req->req.getMethod().equals("GET")).permitAll()//hasAnyAuthority("USER")
//                    .anyRequest().hasAnyAuthority("ADMIN")
                    .anyRequest().fullyAuthenticated()
                .and()
                .csrf().disable()
                    .logout()
                    .logoutUrl("/logout");
        //@formatter:on
    }
}
