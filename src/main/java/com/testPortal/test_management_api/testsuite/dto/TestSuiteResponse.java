package com.testPortal.test_management_api.testsuite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
@Data
@AllArgsConstructor
public class TestSuiteResponse {
    private Integer id;
    private String name;
    private Integer projectId; // We include the parent project's ID for context.
    private long totalTestCases;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
