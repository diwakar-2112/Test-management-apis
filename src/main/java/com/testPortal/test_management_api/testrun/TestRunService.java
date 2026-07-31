package com.testPortal.test_management_api.testrun;


import com.testPortal.test_management_api.exception.ResourceNotFoundException; // Import!
import com.testPortal.test_management_api.testsuite.TestSuite;
import com.testPortal.test_management_api.testsuite.TestSuiteRepository;
import com.testPortal.test_management_api.testrun.dto.*;
import com.testPortal.test_management_api.user.User;
import com.testPortal.test_management_api.user.UserRepository;
import org.aspectj.weaver.ast.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.testPortal.test_management_api.common.PageInfo;
import com.testPortal.test_management_api.common.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

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

    public PagedResponse<TestRunResponse> getAllTestRuns(TestRunSearchCriteria criteria,int page,int size,String sortBy,String sortDir){

        // check who is making the request
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(()->new RuntimeException(("User Not Found")));

        //if ths user is a TESTER  we OVERWRITE whatever assigneeId they passed in the URL.
        // We force it to be their own ID
        if("ROLE_TESTER".equals(currentUser.getRole())){
            criteria.setAssigneeId(currentUser.getId());
        }
        // 1. Create the Sort and Pageable objects
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);

        //2. Dynamic specification
        Specification<TestRun> spec = TestRunSpecifications.withDynamicQuery(criteria);

        //3. Fetch the filtered and paginated data from db
        Page<TestRun> testRunPage = testRunRepository.findAll(spec,pageable);

        //4. Convert Entities to DTOs
        List<TestRunResponse> content = testRunPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        //5. Package into custom Paged Response
        PageInfo pageInfo = new PageInfo(
                testRunPage.getNumber(),
                testRunPage.getTotalPages(),
                testRunPage.getTotalElements(),
                testRunPage.getSize()
        );

        return new PagedResponse<>(content,pageInfo);

    }
}