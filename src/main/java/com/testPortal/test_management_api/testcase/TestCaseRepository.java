package com.testPortal.test_management_api.testcase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Integer> {
    // Using the same derived query method pattern as before.
    // Spring will automatically create a query that finds all TestCase entities
    // where the 'testSuite' field's 'id' property matches the given testSuiteId.
    // SQL Equivalent: "SELECT * FROM test_cases WHERE test_suite_id = ?"
    List<TestCase> findByTestSuiteId(Integer testSuiteId);
    Page<TestCase> findByTestSuiteId(Integer testSuiteId, Pageable pageable);
}
