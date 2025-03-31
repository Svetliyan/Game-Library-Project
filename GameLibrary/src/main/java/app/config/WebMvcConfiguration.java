package app.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
public class WebMvcConfiguration implements WebMvcConfigurer {
    // SecurityFilterChain - начин, по който Spring Security разбира как да се прилага за нашето приложение
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Позволяване на достъп до статични ресурси и някои публични страници
                .authorizeHttpRequests(matchers -> matchers
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/register").permitAll() // Пример за публични страници
                        .anyRequest().authenticated() // Всички останали заявки изискват автентикация
                )
                // Настройка на форма за вход
                .formLogin(form -> form
                        .loginPage("/login") // Страница за вход
                        .defaultSuccessUrl("/profile", true) // Пренасочване след успешен вход
                        .failureUrl("/login?error") // Пренасочване при неуспешен вход
                        .permitAll() // Позволява достъп до страницата за вход
                )
                // Настройка на излизане от системата
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/") // Пренасочване след излизане
                )
                // Защита на CSRF
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

}
