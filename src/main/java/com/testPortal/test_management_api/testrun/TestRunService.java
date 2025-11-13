package com.testPortal.test_management_api.testrun;

import com.testPortal.test_management_api.testcase.TestCase;
import com.testPortal.test_management_api.testsuite.TestSuite;
import com.testPortal.test_management_api.testsuite.TestSuiteRepository;
import com.testPortal.test_management_api.testrun.dto.*;
import com.testPortal.test_management_api.user.User;
import com.testPortal.test_management_api.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestRunService {

    private final TestRunRepository testRunRepository;
    private final TestResultRepository testResultRepository;
    private final TestSuiteRepository testSuiteRepository;
    private final UserRepository userRepository;

    public TestRunService(TestRunRepository testRunRepository,
                          TestResultRepository testResultRepository,
                          TestSuiteRepository testSuiteRepository,
                          UserRepository userRepository) {
        this.testRunRepository = testRunRepository;
        this.testResultRepository = testResultRepository;
        this.testSuiteRepository = testSuiteRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new TestRun from an existing TestSuite.
     * This method copies all test cases from the suite into new TestResult entries.
     */
    @Transactional // Ensures that this entire method runs in a single database transaction.
    public TestRunResponse startTestRunFromSuite(Integer suiteId, CreateTestRunRequest request) {
        // 1. Find the parent TestSuite and its associated TestCases.
        TestSuite testSuite = testSuiteRepository.findById(suiteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TestSuite not found with id: " + suiteId));

        // 2. Create the main TestRun object.
        TestRun testRun = new TestRun();
        testRun.setName(request.getName());
        testRun.setStatus(TestRunStatus.NOT_STARTED);
        testRun.setProject(testSuite.getProject()); // Link the run to the same project as the suite.

        // 3. For each TestCase in the suite, create a corresponding TestResult (a snapshot).
        List<TestResult> testResults = testSuite.getTestCases().stream()
                .map(testCase -> {
                    TestResult result = new TestResult();
                    result.setStatus(TestResultStatus.NOT_RUN);
                    // Copy data from TestCase to TestResult
                    result.setTitle(testCase.getTitle());
                    result.setDescription(testCase.getDescription());
                    result.setSteps(testCase.getSteps());
                    result.setExpectedResult(testCase.getExpectedResult());
                    result.setTestRun(testRun); // Link the result back to the parent run.
                    return result;
                }).collect(Collectors.toList());

        // 4. Set the list of results on the run.
        testRun.setTestResults(testResults);

        // 5. Save the TestRun. Thanks to `cascade = CascadeType.ALL`, all associated TestResults will be saved too.
        TestRun savedTestRun = testRunRepository.save(testRun);

        // 6. Convert to a DTO and return.
        return convertToResponse(savedTestRun);
    }

    /**
     * Assigns an existing TestRun to a User.
     */
    public TestRunResponse assignTestRun(Integer runId, Integer userId) {
        TestRun testRun = testRunRepository.findById(runId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TestRun not found with id: " + runId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));

        testRun.setAssignee(user);
        TestRun savedTestRun = testRunRepository.save(testRun);
        return convertToResponse(savedTestRun);
    }

    /**
     * Retrieves the details of a single TestRun.
     */
    public Optional<TestRunResponse> getTestRunDetails(Integer runId) {
        return testRunRepository.findById(runId).map(this::convertToResponse);
    }

    /**
     * Updates the status and comments of a single TestResult.
     * Also updates the parent TestRun's status if necessary.
     */
    @Transactional
    public TestResultResponse updateTestResult(Integer resultId, UpdateTestResultRequest request) {
        // 1. Find the specific result to update.
        TestResult testResult = testResultRepository.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TestResult not found with id: " + resultId));

        // 2. Update its properties.
        testResult.setStatus(request.getStatus());
        testResult.setComments(request.getComments());
        TestResult savedResult = testResultRepository.save(testResult);

        // 3. Update the parent run's overall status.
        updateTestRunStatus(savedResult.getTestRun());

        return convertToResultResponse(savedResult);
    }

    // --- Private Helper Methods ---

    /**
     * Checks all results in a run and updates the run's status accordingly.
     */
    private void updateTestRunStatus(TestRun testRun) {
        // Reload the run to get all its results
        TestRun runWithResults = testRunRepository.findById(testRun.getId()).get();
        List<TestResult> results = runWithResults.getTestResults();

        // If any result is not in the "NOT_RUN" state, the run is "IN_PROGRESS".
        boolean isInProgress = results.stream().anyMatch(r -> r.getStatus() != TestResultStatus.NOT_RUN);

        // If all results are "finished" (i.e., not "NOT_RUN"), the run is "COMPLETED".
        boolean isCompleted = results.stream().allMatch(r -> r.getStatus() != TestResultStatus.NOT_RUN);

        if (isCompleted) {
            runWithResults.setStatus(TestRunStatus.COMPLETED);
        } else if (isInProgress) {
            runWithResults.setStatus(TestRunStatus.IN_PROGRESS);
        } else {
            runWithResults.setStatus(TestRunStatus.NOT_STARTED);
        }
        testRunRepository.save(runWithResults);
    }

    /**
     * Converts a TestRun entity into a TestRunResponse DTO.
     */
    private TestRunResponse convertToResponse(TestRun testRun) {
        // Handle the assignee (it could be null)
        TestRunResponse.AssigneeResponse assigneeResponse = null;
        if (testRun.getAssignee() != null) {
            assigneeResponse = new TestRunResponse.AssigneeResponse(
                    testRun.getAssignee().getId(),
                    testRun.getAssignee().getUsername()
            );
        }

        // Convert the list of TestResult entities to TestResultResponse DTOs
        List<TestResultResponse> resultResponses = testRun.getTestResults().stream()
                .map(this::convertToResultResponse)
                .collect(Collectors.toList());

        return new TestRunResponse(
                testRun.getId(),
                testRun.getName(),
                testRun.getStatus(),
                testRun.getCreatedAt(),
                testRun.getProject().getId(),
                assigneeResponse,
                resultResponses
        );
    }

    /**
     * Converts a single TestResult entity into a TestResultResponse DTO.
     */
    private TestResultResponse convertToResultResponse(TestResult testResult) {
        return new TestResultResponse(
                testResult.getId(),
                testResult.getStatus(),
                testResult.getComments(),
                testResult.getTitle(),
                testResult.getDescription(),
                testResult.getSteps(),
                testResult.getExpectedResult()
        );
    }
}