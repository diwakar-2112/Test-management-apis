package com.testPortal.test_management_api.user;

import com.testPortal.test_management_api.administration.Role;
import com.testPortal.test_management_api.administration.RoleRepository;
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
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,RoleRepository roleRepository){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.roleRepository=roleRepository;
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

        Role defaultRole = roleRepository.findByRoleName("TESTER")
                .orElseThrow(() -> new RuntimeException("Default Role not found"));
        newUser.setRole(defaultRole);

        return userRepository.save(newUser);
    }

    public List<UserLookupResponse> getAllUserLookups(){

        return userRepository.findAll()
                .stream()
                .map(user-> new UserLookupResponse(user.getId(),user.getUsername()))
                .collect(Collectors.toList());
    }
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
