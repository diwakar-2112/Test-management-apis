package com.testPortal.test_management_api.testsuite;

import com.testPortal.test_management_api.project.Project;

import com.testPortal.test_management_api.testcase.TestCase;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "test_suites")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestSuite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    // @ManyToOne signifies that many TestSuite entities can be associated with one Project.
    @ManyToOne(fetch = FetchType.LAZY) // (1)

    // @JoinColumn specifies the foreign key column in THIS table ("test_suites").
    // The 'name' attribute is the name of the column that will be created: "project_id".
    // 'referencedColumnName' is the column in the OTHER table ("projects") that this refers to, which is "id".

    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false) // (2)
    private Project project;

    @OneToMany(
            mappedBy = "testSuite",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TestCase> testCases;


}
