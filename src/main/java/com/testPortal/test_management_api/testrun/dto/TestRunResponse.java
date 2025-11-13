package com.testPortal.test_management_api.testrun.dto;

import com.testPortal.test_management_api.testrun.TestRunStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class TestRunResponse {
    private Integer id;
    private String name;
    private TestRunStatus status;
    private LocalDateTime createdAt;
    private Integer projectId;
    private AssigneeResponse assignee; // Nested DTO for assignee info
    private List<TestResultResponse> testResults;

    // A simple inner DTO to avoid exposing the entire User entity
    @Data
    @AllArgsConstructor
    public static class AssigneeResponse {
        private Integer id;
        private String username;
    }
}