package com.testPortal.test_management_api.project;
//
////Phase 1
////import lombok.AllArgsConstructor;
////import lombok.Data;
////import lombok.NoArgsConstructor;
////
////// @Data is a Lombok annotation that generates all the boilerplate:
////// getters, setters, toString(), equals(), and hashCode().
////@Data
////
////// @AllArgsConstructor is a Lombok annotation that generates a constructor with all arguments.
////@AllArgsConstructor
////
////// @NoArgsConstructor is a Lombok annotation that generates an empty constructor.
////@NoArgsConstructor
////
////public class Project {
////    private Integer id;
////    private String name;
////    private String description;
////}
//
////Phase 2
//import jakarta.persistence.*; // This is the new import for JPA annotations
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//
//
////@Entity tells JPA that this class is a database entity and should be mapped to a table.
//@Entity
//// @Table(name = "projects") explicitly sets the name of our database table to "projects".
//// If we didn't add this, JPA would name it "project" by default
//@Table(name = "projects")
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class Project{
//
//    //@Id marks this field as the primary key for the table
//    // Every entity MUST have a primary key
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    // By default, JPA will map this field to a column named "name" in the "projects" table.
//    private String name;
//
//    // By default, JPA will map this field to a column named "description".
//    private String description;
//}
//

//Phase 3 -> to add testsuites in Project
import com.testPortal.test_management_api.testsuite.TestSuite; // Import the TestSuite entity
import com.fasterxml.jackson.annotation.JsonManagedReference; // We will use this later

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;


    // @OneToMany signifies that one Project can have many TestSuite entities.
    // The 'mappedBy = "project"' attribute is the most important part here.
    // It tells JPA: "This is the PARENT side of the relationship. Don't create
    // a join column here. Instead, look at the 'project' field in the
    // TestSuite class to find the foreign key configuration."

    // 'cascade = CascadeType.ALL' means that if we save, update, or delete a Project,
    // the related TestSuites will be affected as well. For example, deleting
    // a project will automatically delete all of its test suites.
    //
    // 'orphanRemoval = true' is a companion to cascade. It ensures that if we
    // remove a TestSuite from this list, it will be deleted from the database.

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSuite> testSuites; // (1)
}