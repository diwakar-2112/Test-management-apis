package com.testPortal.test_management_api.testcase;

import com.testPortal.test_management_api.testcase.dto.CreateTestCaseRequest;
import com.testPortal.test_management_api.testcase.dto.TestCaseResponse;
import com.testPortal.test_management_api.testsuite.TestSuite;
import com.testPortal.test_management_api.testsuite.TestSuiteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Service
public class TestCaseService {
    // We need the repositories for both the child (TestCase) and the parent (TestSuite).
    private final TestCaseRepository testCaseRepository;
    private final TestSuiteRepository testSuiteRepository;

    public TestCaseService(TestCaseRepository testCaseRepository,TestSuiteRepository testSuiteRepository){
        this.testCaseRepository=testCaseRepository;
        this.testSuiteRepository=testSuiteRepository;
    }
    /**
     * Creates a new TestCase and associates it with a parent TestSuite.
     */
    public TestCaseResponse createTestCaseForSuite(Integer suiteId,CreateTestCaseRequest request){
        // 1. Find the parent TestSuite. If it doesn't exist, throw a 404 error.
        TestSuite testSuite = testSuiteRepository.findById(suiteId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"TestSuite not found with id:"+suiteId));
        // 2. Create a new TestCase entity from the DTO.
        TestCase newTestCase  = convertToEntity(request);

        // 3. Set the parent TestSuite on the child to create the link.
        newTestCase.setTestSuite(testSuite);

        // 4. Save the new TestCase to the database.
        TestCase savedTestCase = testCaseRepository.save(newTestCase);

        //5 convert the saved entity to response DTO and return
        return convertToResponse(savedTestCase);
    }
    /**
     * Finds all TestCases for a given TestSuite ID.
     */
    public List<TestCaseResponse> getTestCaseForSuit(Integer suiteId){
        //check if the parent test suite existed
        if(!testSuiteRepository.existsById(suiteId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Testsuite not found with the id:"+suiteId);
        }

        // 2. Use  custom repository method to find all the children.
        return  testCaseRepository.findByTestSuiteId(suiteId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }


//    helper methods

    private TestCase convertToEntity(CreateTestCaseRequest request) {
        TestCase testCase = new TestCase();
        // Use the correct getter methods from the request DTO
        testCase.setTitle(request.getTitle());
        testCase.setDescription(request.getDescription());
        testCase.setSteps(request.getSteps());
        testCase.setExpectedResult(request.getExpectedResult());
        return testCase;
    }
    private TestCaseResponse convertToResponse(TestCase testCase) {
        return new TestCaseResponse(
                testCase.getId(),
                testCase.getTitle(),
                testCase.getDescription(),
                testCase.getSteps(),
                testCase.getExpectedResult(),
                testCase.getTestSuite().getId() // Get the parent ID
        );
    }
}

