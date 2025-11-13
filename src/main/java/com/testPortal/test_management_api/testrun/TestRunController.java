package com.testPortal.test_management_api.testrun;

import com.testPortal.test_management_api.testrun.dto.CreateTestRunRequest;
import com.testPortal.test_management_api.testrun.dto.TestRunResponse;
import com.testPortal.test_management_api.testrun.dto.TestResultResponse;
import com.testPortal.test_management_api.testrun.dto.UpdateTestResultRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
// We define two base paths.
// 1. `/api/testsuites/{suiteId}/testruns` is used specifically for creating a run from a suite.
// 2. `/api/testruns` is a top-level path for all other actions related to runs and results.
@RequestMapping({"/api/testsuites/{suiteId}/testruns", "/api/testruns"})
public class TestRunController {

    private final TestRunService testRunService;

    public TestRunController(TestRunService testRunService) {
        this.testRunService = testRunService;
    }

    /**
     * Handles the request to start a new test run from an existing test suite.
     * API Endpoint: POST /api/testsuites/{suiteId}/testruns
     *
     * @param suiteId The ID of the test suite to create the run from.
     * @param request The request body containing the name for the new test run.
     * @return A detailed response object for the newly created test run.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestRunResponse startTestRun(@PathVariable Integer suiteId, @Valid @RequestBody CreateTestRunRequest request) {
        return testRunService.startTestRunFromSuite(suiteId, request);
    }

    /**
     * Handles the request to assign a test run to a specific user.
     * API Endpoint: POST /api/testruns/{runId}/assign?userId={userId}
     *
     * @param runId  The ID of the test run to be assigned.
     * @param userId The ID of the user to assign the run to.
     * @return The updated test run details with the assignee information.
     */
    @PostMapping("/{runId}/assign")
    public TestRunResponse assignTestRun(@PathVariable Integer runId, @RequestParam Integer userId) {
        return testRunService.assignTestRun(runId, userId);
    }

    /**
     * Handles the request to retrieve the full details of a specific test run.
     * API Endpoint: GET /api/testruns/{runId}
     *
     * @param runId The ID of the test run to retrieve.
     * @return The detailed test run object, or a 404 Not Found error if it doesn't exist.
     */
    @GetMapping("/{runId}")
    public TestRunResponse getTestRunDetails(@PathVariable Integer runId) {
        return testRunService.getTestRunDetails(runId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TestRun not found with id: " + runId));
    }

    /**
     * Handles the request to update the status and comments of a single test result within a run.
     * API Endpoint: PUT /api/testruns/results/{resultId}
     *
     * @param resultId The ID of the specific test result to update.
     * @param request  The request body containing the new status and comments.
     * @return The updated test result details.
     */
    @PutMapping("/results/{resultId}")
    public TestResultResponse updateResult(@PathVariable Integer resultId, @Valid @RequestBody UpdateTestResultRequest request) {
        return testRunService.updateTestResult(resultId, request);
    }
}