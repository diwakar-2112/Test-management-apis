package com.testPortal.test_management_api.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAdminUserRequest {

    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;

    @NotNull(message = "Status is required")
    private UserStatus status;

    @NotNull(message = "Role ID is required")
    private Integer roleId;
}
