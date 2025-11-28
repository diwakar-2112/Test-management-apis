package com.testPortal.test_management_api.testrun;

import com.testPortal.test_management_api.testrun.dto.CreateTestRunRequest;
import com.testPortal.test_management_api.testrun.dto.TestRunResponse;
import com.testPortal.test_management_api.testrun.dto.TestResultResponse;
import com.testPortal.test_management_api.testrun.dto.UpdateTestResultRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/testsuites/{suiteId}/testruns", "/api/testruns"})
public class TestRunController {

    private final TestRunService testRunService;

    public TestRunController(TestRunService testRunService) {
        this.testRunService = testRunService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestRunResponse startTestRun(@PathVariable Integer suiteId, @Valid @RequestBody CreateTestRunRequest request) {
        return testRunService.startTestRunFromSuite(suiteId, request);
    }

    @PostMapping("/{runId}/assign")
    public TestRunResponse assignTestRun(@PathVariable Integer runId, @RequestParam Integer userId) {
        return testRunService.assignTestRun(runId, userId);
    }

    // SIMPLIFIED: No more .orElseThrow()
    @GetMapping("/{runId}")
    public TestRunResponse getTestRunDetails(@PathVariable Integer runId) {
        return testRunService.getTestRunDetails(runId);
    }

    @PutMapping("/results/{resultId}")
    public TestResultResponse updateResult(@PathVariable Integer resultId, @Valid @RequestBody UpdateTestResultRequest request) {
        return testRunService.updateTestResult(resultId, request);
    }
}