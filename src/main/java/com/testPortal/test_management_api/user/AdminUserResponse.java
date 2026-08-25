package com.testPortal.test_management_api.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class AdminUserResponse {
    private Integer id;
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;
    private UserStatus status;
    private Integer roleId;
    private String roleName;
}
