//--> This code is for first phase

//package com.testPortal.test_management_api.project;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/projects")
//
//public class ProjectController {
//
//    //we declare the service we need
//    private final ProjectService projectService;
//
//    // This is called "Constructor Injection".
//    // Spring sees that the controller needs a ProjectService to be created,
//    // so it automatically provides the one we created earlier. This is a core concept of Spring.
//    public ProjectController(ProjectService projectService){
//        this.projectService=projectService;
//    }
//
//    @GetMapping
//    public List<Project>findAll(){
//        return projectService.findAll();
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Project create(@RequestBody Project project){
//        return projectService.create(project);
//    }
//
//    // @GetMapping("/{id}") defines the URL. The part in curly braces {} is a variable.
//    @GetMapping("/{id}")
//    // The @PathVariable("id") annotation tells Spring to take the "id" value from the URL
//    // and pass it into the 'id' parameter of our method.
//    public Project findById(@PathVariable Integer id){
//        return projectService.findById(id)
//        // If the Optional from the service is empty, we throw an exception.
//        // ResponseStatusException is a special Spring exception that automatically
//        // sends back the correct HTTP 404 Not Found error code and a message.
//                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Project not found with id:"+id));
//    }
//
//    @PutMapping("/{id}")
//    public Project update(@PathVariable Integer id, @RequestBody Project updateProject){
//        // We use the service to update the project.
//        // If the project is not found, the service will return an empty Optional,
//        // and we throw a 404 error, just like in our findById method.
//        return projectService.update(id,updateProject).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Project not found with id:"+id));
//    }
//
//    // @DeleteMapping marks this method to handle HTTP DELETE requests.
//    @DeleteMapping("/{id}")
//    // @ResponseStatus(HttpStatus.NO_CONTENT) is important. It tells the client that the
//    // operation was successful, but there is no data to return in the response body.
//    // This is the standard for DELETE operations.
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable Integer id){
//        projectService.delete(id);
//    }
//
//}

//This is for 2nd Phase
package com.testPortal.test_management_api.project;
import com.testPortal.test_management_api.project.dto.CreateProjectRequest;
import com.testPortal.test_management_api.project.dto.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> findAll(){
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable Integer id){
        return projectService.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Project not found with id:"+ id));
    }

    // The @Valid annotation is NEW. It tells Spring to check the validation rules
    // we defined in the CreateProjectRequest DTO.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request){
        return projectService.create(request);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Integer id, @Valid @RequestBody CreateProjectRequest request){
        return projectService.update(id,request).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Project not found with id:"+id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        projectService.delete(id);
    }
}

