package com.testPortal.test_management_api.administration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class RoleDto {

    @JsonProperty("roleId")
    private Integer id;
    private String roleName;
    private List<RoleModuleAccessDto> moduleAccess;
}
