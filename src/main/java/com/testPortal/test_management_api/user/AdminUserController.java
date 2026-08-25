package com.testPortal.test_management_api.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.testPortal.test_management_api.common.PagedResponse;

import java.util.List;


@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('USER_MGMT_CREATE')")
    public AdminUserResponse createUser(@Valid @RequestBody CreateAdminUserRequest request) {
        return userService.createAdminUser(request);
    }

    @GetMapping
//    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('USER_MGMT_LIST')")
    public PagedResponse<AdminUserResponse> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "id") String sortBy,
                                               @RequestParam(defaultValue = "desc") String sortDir) {
        return userService.getAllAdminUsers(page, size, sortBy, sortDir);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('USER_MGMT_EDIT')")
    public AdminUserResponse updateUser(@PathVariable Integer id, @Valid @RequestBody UpdateAdminUserRequest request) {
        return userService.updateAdminUser(id, request);
    }
}
