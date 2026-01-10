package com.testPortal.test_management_api.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String userName;
    private String role;

    public LoginResponse(String accessToken,String userName,String role)
    {
        this.accessToken = accessToken;
        this.userName=userName;
        this.role=role;
    }
}
