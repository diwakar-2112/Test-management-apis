package com.testPortal.test_management_api.testrun;

import com.testPortal.test_management_api.exception.ResourceNotFoundException; // Import!
import com.testPortal.test_management_api.testsuite.TestSuite;
import com.testPortal.test_management_api.testsuite.TestSuiteRepository;
import com.testPortal.test_management_api.testrun.dto.*;
import com.testPortal.test_management_api.user.User;
import com.testPortal.test_management_api.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Transactional
    public TestRunResponse startTestRunFromSuite(Integer suiteId, CreateTestRunRequest request) {
        TestSuite testSuite = testSuiteRepository.findById(suiteId)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite not found with id: " + suiteId));

        // ... (rest of the method is unchanged)
        TestRun testRun = new TestRun();
        testRun.setName(request.getName());
        testRun.setStatus(TestRunStatus.NOT_STARTED);
        testRun.setProject(testSuite.getProject());

        List<TestResult> testResults = testSuite.getTestCases().stream()
                .map(testCase -> {
                    TestResult result = new TestResult();
                    result.setStatus(TestResultStatus.NOT_RUN);
                    result.setTitle(testCase.getTitle());
                    result.setDescription(testCase.getDescription());
                    result.setSteps(testCase.getSteps());
                    result.setExpectedResult(testCase.getExpectedResult());
                    result.setTestRun(testRun);
                    return result;
                }).collect(Collectors.toList());

        testRun.setTestResults(testResults);
        TestRun savedTestRun = testRunRepository.save(testRun);
        return convertToResponse(savedTestRun);
    }

    public TestRunResponse assignTestRun(Integer runId, Integer userId) {
        TestRun testRun = testRunRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("TestRun not found with id: " + runId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        testRun.setAssignee(user);
        TestRun savedTestRun = testRunRepository.save(testRun);
        return convertToResponse(savedTestRun);
    }

    public TestRunResponse getTestRunDetails(Integer runId) {
        TestRun testRun = testRunRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("TestRun not found with id: " + runId));
        return convertToResponse(testRun);
    }

    @Transactional
    public TestResultResponse updateTestResult(Integer resultId, UpdateTestResultRequest request) {
        TestResult testResult = testResultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("TestResult not found with id: " + resultId));

        testResult.setStatus(request.getStatus());
        testResult.setComments(request.getComments());
        TestResult savedResult = testResultRepository.save(testResult);

        updateTestRunStatus(savedResult.getTestRun());
        return convertToResultResponse(savedResult);
    }

    // ... (private helper methods are unchanged)
    private void updateTestRunStatus(TestRun testRun) {
        TestRun runWithResults = testRunRepository.findById(testRun.getId()).get();
        List<TestResult> results = runWithResults.getTestResults();
        boolean isInProgress = results.stream().anyMatch(r -> r.getStatus() != TestResultStatus.NOT_RUN);
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

    private TestRunResponse convertToResponse(TestRun testRun) {
        TestRunResponse.AssigneeResponse assigneeResponse = null;
        if (testRun.getAssignee() != null) {
            assigneeResponse = new TestRunResponse.AssigneeResponse(
                    testRun.getAssignee().getId(),
                    testRun.getAssignee().getUsername()
            );
        }
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