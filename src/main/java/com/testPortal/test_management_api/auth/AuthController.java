package com.testPortal.test_management_api.auth; // Your package name

import com.testPortal.test_management_api.security.CustomUserDetailsService;
import com.testPortal.test_management_api.user.User;
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
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    // --- NEW FIELDS TO INJECT ---
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    ///  for Google authentication
    private final GoogleAuthService googleAuthService;
    private final CustomUserDetailsService customUserDetailsService;

    // --- UPDATED CONSTRUCTOR TO ACCEPT NEW DEPENDENCIES ---
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,GoogleAuthService googleAuthService, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.googleAuthService = googleAuthService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","User Registered Successfully"));
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

        // 4. --- NEW: Get User Details ---
        // The "Principal" is the UserDetails object returned by your CustomUserDetailsService
        UserDetails  userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream().findFirst().get().getAuthority();

        // Return the token in the response.
        return ResponseEntity.ok(new LoginResponse(jwt,userName,role));
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request){
        // 1. Verify the Google token and get our DB User
        User user = googleAuthService.verifyGoogleTokenAndProcessUser(request.getIdToken());

        // 2. Load their UserDetails so Spring Security can use it
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        // 3. Create an Authentication object (bypassing the password check since Google authenticated them)
        Authentication authentication= new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //4 Generate standard jwt token
        String jwt = tokenProvider.generateToken(authentication);

        //5 Extract userName and role
        String userName = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream().findFirst().get().getAuthority();

        //5 Return the token
        return ResponseEntity.ok(new LoginResponse(jwt,userName,role));
    }



}