package com.testPortal.test_management_api.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


// This DTO defines the data that our API will SEND back to the user.
// We are explicitly defining what is "safe" to show the public.
@Data
@AllArgsConstructor

public class ProjectResponse {
    private Integer id;
    private String name;
    private String description;
}
