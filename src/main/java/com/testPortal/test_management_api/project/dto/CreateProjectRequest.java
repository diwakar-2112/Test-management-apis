package com.testPortal.test_management_api.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// This DTO defines the data required to CREATE or UPDATE a project.
@Data
public class CreateProjectRequest {

    // @NotBlank checks that the value is not null and not just empty spaces.
    // The `message` is what the user will see if this rule is broken.
    @NotBlank(message = "Project name cannot be blank.")

    // @Size checks that the string length is within a certain range.
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters.")
    private String name;

    @Size(max=500, message = "Description cannot be longer than 500wrds")
    private String description;

}
