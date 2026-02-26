package com.testPortal.test_management_api.testcase;

import com.testPortal.test_management_api.exception.ResourceNotFoundException;
import com.testPortal.test_management_api.testcase.dto.CreateTestCaseRequest;
import com.testPortal.test_management_api.testcase.dto.TestCaseResponse;
import com.testPortal.test_management_api.testcase.dto.UpdateTestCaseRequest;
import com.testPortal.test_management_api.testsuite.TestSuite;
import com.testPortal.test_management_api.testsuite.TestSuiteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

//for pagination
 import com.testPortal.test_management_api.common.PageInfo;
 import com.testPortal.test_management_api.common.PagedResponse;
 import org.springframework.data.domain.Page;
 import org.springframework.data.domain.PageRequest;
 import org.springframework.data.domain.Pageable;
 import org.springframework.data.domain.Sort;


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
//    public List<TestCaseResponse> getTestCaseForSuit(Integer suiteId){
//        //check if the parent test suite existed
//        if(!testSuiteRepository.existsById(suiteId)){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Testsuite not found with the id:"+suiteId);
//        }
//
//        // 2. Use  custom repository method to find all the children.
//        return  testCaseRepository.findByTestSuiteId(suiteId).stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//
//    }

//    with pagination
    public PagedResponse<TestCaseResponse> getTestCasesForSuite(Integer suiteId, int page, int size, String sortBy, String sortDir) {
        if (!testSuiteRepository.existsById(suiteId)) {
            throw new ResourceNotFoundException("TestSuite not found with id: " + suiteId);
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TestCase> testCasePage = testCaseRepository.findByTestSuiteId(suiteId, pageable);

        List<TestCaseResponse> content = testCasePage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo(
                testCasePage.getNumber(),
                testCasePage.getTotalPages(),
                testCasePage.getTotalElements(),
                testCasePage.getSize()
        );

        return new PagedResponse<>(content, pageInfo);
    }
    public Optional<TestCaseResponse> updateTestCase(Integer caseId, UpdateTestCaseRequest request) {
        // 1. Find the existing test case in the database by its unique ID.
        return testCaseRepository.findById(caseId)
                .map(existingTestCase -> { // .map() executes this code block if the test case is found.
                    // 2. Update the properties of the found entity with the data from the request.
                    existingTestCase.setTitle(request.getTitle());
                    existingTestCase.setDescription(request.getDescription());
                    existingTestCase.setSteps(request.getSteps());
                    existingTestCase.setExpectedResult(request.getExpectedResult());

                    // 3. Save the updated entity back to the database.
                    TestCase updatedTestCase = testCaseRepository.save(existingTestCase);

                    // 4. Convert the updated entity to a response DTO and return it.
                    return convertToResponse(updatedTestCase);
                });
    }
    public boolean deleteTestCase(Integer caseId) {
        // 1. Check if a test case with the given ID exists in the database.
        if (testCaseRepository.existsById(caseId)) {
            // 2. If it exists, delete it.
            testCaseRepository.deleteById(caseId);
            return true;
        }
        // 3. If it does not exist, do nothing and return false.
        return false;
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

