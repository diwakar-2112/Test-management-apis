package com.testPortal.test_management_api.user;

import com.testPortal.test_management_api.administration.Role;
import com.testPortal.test_management_api.administration.RoleRepository;
import com.testPortal.test_management_api.auth.RegisterRequest;
import com.testPortal.test_management_api.common.PageInfo;
import com.testPortal.test_management_api.common.PagedResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.testPortal.test_management_api.user.UserLookupResponse;

//pagination
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    //create user
    public AdminUserResponse createAdminUser(CreateAdminUserRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new RuntimeException("Username already exist");
        }
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new RuntimeException("Password do not match");
        }
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(()->new RuntimeException("Role not found with Id: "+request.getRoleId()));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setStatus(request.getStatus());
        user.setRole(role);

        User savedUser = userRepository.save(user);
        return convertToAdminResponse(savedUser);
    }

    //update user details
    public AdminUserResponse updateAdminUser(Integer id, UpdateAdminUserRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("User not found with id: "+id));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(()-> new RuntimeException("Role not found with id: "+request.getRoleId()));
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setStatus(request.getStatus());
        user.setRole(role);

        User updatedUser = userRepository.save(user);
        return convertToAdminResponse(updatedUser);
    }

    //get all user
//    public List<AdminUserResponse> getAllAdminUsers(){
//        return userRepository.findAll().stream()
//                .map(this::convertToAdminResponse)
//                .collect(Collectors.toList());
//    }

    public PagedResponse<AdminUserResponse> getAllAdminUsers(int page, int size,String sortBy,String sortDir){
        //sorting
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> usersPage = userRepository.findAll(pageable);

        List<AdminUserResponse> content = usersPage.getContent().stream()
                .map(this::convertToAdminResponse)
                .collect(Collectors.toList());

        // 5. Package the pagination metadata
        PageInfo pageInfo = new PageInfo(
                usersPage.getNumber(),
                usersPage.getTotalPages(),
                usersPage.getTotalElements(),
                usersPage.getSize()
        );

        // 6. Return the combined response!
        return new PagedResponse<>(content, pageInfo);
    }


    // Helper method
    private AdminUserResponse convertToAdminResponse(User user) {
        return new AdminUserResponse(
                user.getId(), user.getUsername(), user.getFirstName(),
                user.getMiddleName(), user.getLastName(), user.getEmail(),
                user.getMobile(), user.getStatus(),
                user.getRole().getId(), user.getRole().getRoleName()
        );
    }
}
