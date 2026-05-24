package com.testPortal.test_management_api.user;

import com.testPortal.test_management_api.auth.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.testPortal.test_management_api.user.UserLookupResponse;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }
    public User registerUser(RegisterRequest request){
        //check if username already found
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new RuntimeException("User name already exists");
        }
        User newUser = new User();
        newUser.setUsername(request.getUsername());

        // Hash the password before savingp
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // For now, we'll hardcode new users as "TESTER".
        // Later, you could add logic to handle different roles.
        newUser.setRole("ROLE_TESTER");

        return userRepository.save(newUser);
    }

    public List<UserLookupResponse> getAllUserLookups(){

        return userRepository.findAll()
                .stream()
                .map(user-> new UserLookupResponse(user.getId(),user.getUsername()))
                .collect(Collectors.toList());
    }
}
