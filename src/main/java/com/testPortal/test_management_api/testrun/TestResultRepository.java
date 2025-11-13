package com.testPortal.test_management_api.testrun;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// THIS IS THE CRITICAL LINE THAT WAS LIKELY MISSING OR INCORRECT
public interface TestResultRepository extends JpaRepository<TestResult, Integer> {
    // By extending JpaRepository, this interface automatically inherits the findById() method,
    // as well as save(), deleteById(), findAll(), and many others.
}