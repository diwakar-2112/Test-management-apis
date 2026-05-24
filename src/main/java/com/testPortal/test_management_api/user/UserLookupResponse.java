package com.testPortal.test_management_api.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLookupResponse {
    private Integer id;
    private String userName;
}
