package medicalimageanalyser.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import medicalimageanalyser.application.service.CustomOAuth2UserService;
import medicalimageanalyser.application.service.SecurityService;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final SecurityService securityService;

    private String loginURL = "http://localhost:5173/login";
    private String successURL = "http://localhost:5173/";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/auth/**","/register", "/register.html", "/oauth2/**", "/login/**","/csrf-token").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .loginPage(loginURL)
                .successHandler((request, response, authentication) -> 
                    response.sendRedirect("http://localhost:5173/oauth2/redirect?username=" + authentication.getName())
                )
                .defaultSuccessUrl(successURL)
                .permitAll()
            )
            .formLogin(login -> login
                .loginPage(loginURL).permitAll()
                .defaultSuccessUrl(successURL)
            )
            .logout(logout -> logout.logoutSuccessUrl(loginURL).permitAll())
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http
            .authenticationProvider(authenticationProvider())
            .getSharedObject(AuthenticationManagerBuilder.class)
            .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(securityService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
