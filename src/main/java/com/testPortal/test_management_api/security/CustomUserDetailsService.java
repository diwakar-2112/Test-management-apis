package com.testPortal.test_management_api.security;

import com.testPortal.test_management_api.user.User;
import com.testPortal.test_management_api.user.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService  {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    // This is the only method we need to implement.
    // Spring Security will call this method when a user tries to authenticate.
    @Override
    public UserDetails loadUserByUsername(String username) throws  UsernameNotFoundException{
        // 1. Use our repository to find the user in our database.
            User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("user not found with the username:"+username));

        // 2. Convert our custom User entity into a Spring Security UserDetails object.
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

        // Then, return a new User object that Spring Security can understand.
        // This object requires the username, the HASHED password, and the authorities.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

}
