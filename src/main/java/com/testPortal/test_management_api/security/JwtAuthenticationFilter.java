package com.testPortal.test_management_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. Try to extract the JWT from the request header.
        String jwt = tokenProvider.resolveToken(request);

        // 2. If a token is found and is valid...
        if (jwt != null && tokenProvider.validateToken(jwt)) {
            // 3. ...get the username from the token.
            String username = tokenProvider.getUsernameFromJWT(jwt);

            // 4. Load the user's details from the database.
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // 5. Create an authentication object.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 6. Set the authentication object in Spring Security's context.
            // This is the crucial step that "authenticates" the user for this request.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7. Continue the filter chain.
        filterChain.doFilter(request, response);
    }
}
