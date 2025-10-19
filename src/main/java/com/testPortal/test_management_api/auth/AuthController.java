package com.testPortal.test_management_api.auth;

import com.testPortal.test_management_api.user.User;
import com.testPortal.test_management_api.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    public AuthController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User Registered Successfully");
    }
}
