package com.testPortal.test_management_api.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//// @Configuration indicates that this class contains Spring configuration.
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    // We define a Bean for the PasswordEncoder. A "Bean" is an object managed by Spring.
//    // This makes the PasswordEncoder available for injection anywhere in our application.
//    // We use BCrypt, which is the industry standard for hashing passwords.
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    // This Bean defines our main security rules for HTTP requests.
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//        // Disable CSRF (Cross-Site Request Forgery) protection.
//        // This is common for stateless REST APIs where the client is not a web browser.
//                .csrf(csrf->csrf.disable())
//        // Define the authorization rules for different endpoints.
//                .authorizeHttpRequests(auth->auth
//                        // Permit all requests to the H2 console and our future auth endpoints.
//                        .requestMatchers("/h2-ui/**","/api/auth/**").permitAll()
//                        //All other request must be authenticated (the user must be logged in).
//                        .anyRequest().authenticated()
//                )
//
//                //configure session management to stateless
//                //for a REST API, each request should be independent and not rely on a server-side session.
//                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        // This is needed to allow the H2 console to be viewed in a browser frame.
//            http.headers(headers->headers.frameOptions(frameOptions->frameOptions.sameOrigin()));
//
//            return http.build();
//    }
//}


//updated code

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider; // --- NEW IMPORT ---
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // --- NEW IMPORT ---
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService; // --- NEW FIELD ---

    // --- UPDATED CONSTRUCTOR ---
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- THIS IS THE NEW BEAN THAT FIXES THE PROBLEM ---
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Tell the provider to use our custom user details service to find users
        authProvider.setUserDetailsService(customUserDetailsService);
        // Tell the provider to use our BCrypt password encoder to check passwords
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-ui/**", "/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // --- NEW LINE ---
                // Register our new authentication provider
                .authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}