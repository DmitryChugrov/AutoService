package com.InfoSystem.AutoService.Config;
import com.InfoSystem.AutoService.Model.Users;
import com.InfoSystem.AutoService.Service.UsersService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//  Класс конфигурации авторизации пользователя
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    Метод для разграничения функционала при авторизации
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers("/client/**").hasRole("CLIENT")      // Проверка, есть ли у пользователя роль "Клиент"
                        .requestMatchers("/operatorBD/**").hasRole("OPERATOR")      // Проверка, есть ли у пользователя роль "Администратор БД"
                        .requestMatchers("/manager/**").hasRole("MANAGER")      // Проверка, есть ли у пользователя роль "Менеджер"
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .permitAll())
        .csrf(csrf -> csrf.disable());

        return http.build();
    }
//      Метод для авторизации пользователя
    @Bean
    public UserDetailsService userDetailsService(UsersService usersService) {
        return email -> {
            Users users = usersService.findByEmail(email);
            if (users == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return org.springframework.security.core.userdetails.User.builder()
                    .username(users.getEmail())
                    .password(users.getPassword())
                    .roles(users.getRole())
                    .build();
        };
    }
}
