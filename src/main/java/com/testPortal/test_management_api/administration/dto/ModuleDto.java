package com.testPortal.test_management_api.administration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ModuleDto {
    private Integer moduleId;

    @NotBlank(message = "Module Name is Required")
    private String moduleName;

    @NotBlank(message = "Module Url is required")
    private String moduleUrl;

    @NotBlank(message = "Module key is required")
    private String moduleKey;
}
