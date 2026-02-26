package com.testPortal.test_management_api.testrun;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "test_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING) // Stores the status as a String (e.g., "PASS", "FAIL")
    @Column(nullable = false)
    private TestResultStatus status;

    @Column(columnDefinition = "TEXT")
    private String comments;

    // --- Snapshot of the original TestCase ---
    // We copy this data to keep a historical record. Even if the original
    // TestCase changes later, the record of this run will not be affected.
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String steps;

    @Column(columnDefinition = "TEXT")
    private String expectedResult;

    // --- Relationship ---

    // Establishes that many TestResults must belong to one TestRun.
    // The "test_run_id" column will be created in our "test_results" table
    // to link back to the parent TestRun.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_run_id", referencedColumnName = "id", nullable = false)
    private TestRun testRun;
}
