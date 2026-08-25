package com.testPortal.test_management_api.administration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateRoleRequest {

    @NotBlank(message = "Role name is required")
    private String roleName;

    @NotNull(message = "Module access list cannot be null")
    private List<RoleModuleAccessDto> moduleAccess;
}
