package com.testPortal.test_management_api.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAdminUserRequest {
    @NotBlank(message = "Username is required")
    private String username;

    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;


    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @NotNull(message = "Status is required")
    private UserStatus status;

    @NotNull(message = "Role ID is required")
    private Integer roleId;
}
