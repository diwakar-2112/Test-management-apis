package com.testPortal.test_management_api.testrun;

import com.testPortal.test_management_api.project.Project;
import com.testPortal.test_management_api.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="test_runs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) // Store the enum value as a String (e.g., "IN_PROGRESS")
    @Column(nullable = false)
    private TestRunStatus status;

    @CreationTimestamp // Automatically set the creation time when a new run is created
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // --- Relationships ---

    // Establishes that many TestRuns can belong to one Project.
    // The "project_id" column will be created in our "test_runs" table.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    // A run can be assigned to one user.
    // The "assignee_id" column will be created. It can be null if the run is unassigned.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", referencedColumnName = "id")
    private User assignee;


    // A TestRun has a list of TestResults.
    // If we delete a TestRun, all of its associated results will be deleted too (cascade = CascadeType.ALL).
    @OneToMany(mappedBy = "testRun", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestResult> testResults;
}
