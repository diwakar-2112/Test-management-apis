package com.testPortal.test_management_api.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Data is a Lombok annotation that generates all the boilerplate:
// getters, setters, toString(), equals(), and hashCode().
@Data

// @AllArgsConstructor is a Lombok annotation that generates a constructor with all arguments.
@AllArgsConstructor

// @NoArgsConstructor is a Lombok annotation that generates an empty constructor.
@NoArgsConstructor

public class Project {
    private Integer id;
    private String name;
    private String description;
}
