package com.testPortal.test_management_api.auth; // Your package name

import com.testPortal.test_management_api.user.UserService; // Your package name
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.testPortal.test_management_api.security.JwtTokenProvider; // Your package name

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    // --- NEW FIELDS TO INJECT ---
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    // --- UPDATED CONSTRUCTOR TO ACCEPT NEW DEPENDENCIES ---
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User Registered Successfully");
    }

    // --- NEW LOGIN ENDPOINT ---
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Use the AuthenticationManager to validate the username and password.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Set the authentication object in the SecurityContext.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate the JWT token.
        String jwt = tokenProvider.generateToken(authentication);

        // Return the token in the response.
        return ResponseEntity.ok(new LoginResponse(jwt));
    }
}