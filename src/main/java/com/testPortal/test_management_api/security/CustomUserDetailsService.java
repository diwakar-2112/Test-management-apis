package com.testPortal.test_management_api.security;

import com.testPortal.test_management_api.administration.RoleModuleAccess;
import com.testPortal.test_management_api.user.User;
import com.testPortal.test_management_api.user.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.Collections;
import java.util.ArrayList;
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
    @Transactional
    public UserDetails loadUserByUsername(String username) throws  UsernameNotFoundException{
        // 1. Use our repository to find the user in our database.
            User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("user not found with the username:"+username));

        List<GrantedAuthority> authorities = new ArrayList<>();

        // 2. Add the base Role Name (e.g., "ROLE_SUPER_ADMIN" or "ROLE_TESTER")
        // This acts as our skeleton key for Super Admins!
        String roleName = user.getRole().getRoleName();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

        // 3. Loop through all the checkboxes for this role and generate granular permissions
        if (user.getRole().getModuleAccessList() != null) {
            for (RoleModuleAccess access : user.getRole().getModuleAccessList()) {
                String moduleKey = access.getModule().getModuleKey(); // e.g., "PROJECT"

                if (access.isCanCreate()) authorities.add(new SimpleGrantedAuthority(moduleKey + "_CREATE"));
                if (access.isCanEdit())   authorities.add(new SimpleGrantedAuthority(moduleKey + "_EDIT"));
                if (access.isCanDelete()) authorities.add(new SimpleGrantedAuthority(moduleKey + "_DELETE"));
                if (access.isCanList())   authorities.add(new SimpleGrantedAuthority(moduleKey + "_LIST"));
                if (access.isCanView())   authorities.add(new SimpleGrantedAuthority(moduleKey + "_VIEW"));
            }
        }
        // 4. Return the UserDetails with the massive list of specific permissions
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

}
