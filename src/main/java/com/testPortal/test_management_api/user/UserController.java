package com.testPortal.test_management_api.user;
import com.testPortal.test_management_api.user.UserLookupResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    /**
     * Get api/users/lookup
     */
    @GetMapping("/lookup")
    @PreAuthorize("hasRole('ADMIN')")
    public  List<UserLookupResponse>getAllUsersForLookup(){
        return userService.getAllUserLookups();
    }

}
