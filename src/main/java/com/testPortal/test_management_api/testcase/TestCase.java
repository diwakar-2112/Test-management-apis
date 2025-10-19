package com.testPortal.test_management_api.testcase;

import com.testPortal.test_management_api.testsuite.TestSuite;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "test_cases")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    private String description;

    // By default, a String maps to VARCHAR(255), which can be too short for steps.
    // @Column(columnDefinition = "TEXT") tells the database to use a more appropriate
    // data type for potentially long text.
    @Column(columnDefinition = "TEXT")
    private String steps;

    @Column(columnDefinition = "TEXT")
    private String expectedResult;


    // --- RELATIONSHIP MAPPING ---

    // This is the "Many" side. Many TestCases belong to one TestSuite.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_suite_id", referencedColumnName = "id", nullable = false)
    private TestSuite testSuite;

}
