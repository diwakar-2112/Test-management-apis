package com.testPortal.test_management_api.project;

// Phase 1
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import java.util.Optional;
//// @Service tells Spring that this class is a "Service". It's a stereotype annotation
//// that makes this class a candidate for dependency injection.
//@Service
//
//public class ProjectService {
//    // We move the list of projects here.
//    // We use ArrayList because we need to be able to ADD new projects to this list.
//    private final List<Project> projects = new ArrayList<>();
//
//    // This will act as our fake ID generator, like a database sequence.
//    private final AtomicInteger counter = new AtomicInteger();
//
//
//    // This block runs when the service is first created.
//    // It pre-populates our list with some initial data.
//    public ProjectService() {
//        projects.add(new Project(counter.incrementAndGet(), "E-commerce Website", "Test suite for the new online store."));
//        projects.add(new Project(counter.incrementAndGet(), "Mobile Banking App", "Test suite for the iOS and Android banking app."));
//    }
//
//    /**
//     * Returns a list of all projects.
//     */
//    public List<Project> findAll() {
//        return projects;
//    }
//
//    /**
//     * Creates a new project.
//     * @param project The project data to create.
//     * @return The newly created project with its assigned ID.
//     */
//    public Project create(Project project) {
//        // In a real app, the database would set the ID. Here, we set it manually.
//        project.setId(counter.incrementAndGet());
//        projects.add(project);
//        return project;
//    }
//
//    /*
//     * Finds a single project by its ID.
//     * @param id The ID of the project to find.
//     * @return An Optional containing the project if found, otherwise an empty Optional.
//     */
//    public Optional<Project> findById(Integer id){
//        return projects.stream().filter(p->p.getId().equals(id)).findFirst();
//    }
//
//    /*
//     * Updates an existing project.
//     * @param id The ID of the project to update.
//     * @param updatedProject The project data with the new values.
//     * @return An Optional containing the updated project if it was found, otherwise an empty Optional.
//     */
//    public Optional<Project> update(Integer id, Project updatedProject){
//        // First, find the existing project.
//        Optional<Project> existingProjectOpt = findById(id);
//
//        //if the project exits, update it
//        existingProjectOpt.ifPresent(existingProject->{
//            // Find the index of the existing project in the list.
//            existingProject.setName(updatedProject.getName());
//            existingProject.setDescription(updatedProject.getDescription());
//        });
//        return existingProjectOpt;
//
//    }
//
//    /**
//     * Deletes a project by its ID.
//     * @param id The ID of the project to delete.
//     */
//    public void delete(Integer id){
//        // The removeIf method is a clean way to remove an element from a list
//        // that matches a specific condition.
//        // It will iterate through the list and remove any project where the ID matches.
//        projects.removeIf(project -> project.getId().equals(id));
//    }
//
//}

//Phase 2
//
//import com.testPortal.test_management_api.project.dto.CreateProjectRequest;
//import com.testPortal.test_management_api.project.dto.ProjectResponse;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.Collectors;
//
//@Service
//public class ProjectService {
//    private final List<Project> projects = new ArrayList<>();
//    private final AtomicInteger counter = new AtomicInteger();
//
//    public ProjectService() {
//        projects.add(new Project(counter.incrementAndGet(), "E-commerce Website", "Test suite for the new online store."));
//        projects.add(new Project(counter.incrementAndGet(), "Mobile Banking App", "Test suite for the iOS and Android banking app."));
//    }
//
//    // CHANGE: Now returns List<ProjectResponse>
//    public List<ProjectResponse> findAll() {
//        // This is the translation part.
//        // We use a Java Stream to go through each 'Project' in our list,
//        // create a new 'ProjectResponse' from it, and collect them into a new list.
//        return projects.stream()
//                .map(project -> new ProjectResponse(project.getId(), project.getName(), project.getDescription()))
//                .collect(Collectors.toList());
//    }
//
//    // CHANGE: Now returns Optional<ProjectResponse>
//    public Optional<ProjectResponse> findById(Integer id) {
//        return projects.stream()
//                .filter(p -> p.getId().equals(id))
//                .findFirst()
//                .map(project -> new ProjectResponse(project.getId(), project.getName(), project.getDescription()));
//    }
//
//    // CHANGE: Accepts a DTO and returns a DTO
//    public ProjectResponse create(CreateProjectRequest request) {
//        // Translate from DTO to our internal Project model
//        Project project = new Project();
//        project.setId(counter.incrementAndGet());
//        project.setName(request.getName());
//        project.setDescription(request.getDescription());
//        projects.add(project);
//
//        // Translate from our internal Project model back to a DTO for the response
//        return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
//    }
//
//    // CHANGE: Accepts a DTO and returns an Optional<ProjectResponse>
//    public Optional<ProjectResponse> update(Integer id, CreateProjectRequest request) {
//        // Find the existing project
//        return projects.stream()
//                .filter(p -> p.getId().equals(id))
//                .findFirst()
//                .map(existingProject -> {
//                    // Update the internal model
//                    existingProject.setName(request.getName());
//                    existingProject.setDescription(request.getDescription());
//                    // Translate to a response DTO
//                    return new ProjectResponse(existingProject.getId(), existingProject.getName(), existingProject.getDescription());
//                });
//    }
//
//    public void delete(Integer id) {
//        projects.removeIf(project -> project.getId().equals(id));
//    }
//}

//Phase 3

import com.testPortal.test_management_api.project.dto.CreateProjectRequest;
import com.testPortal.test_management_api.project.dto.ProjectResponse;
import org.springframework.stereotype.Service;
import com.testPortal.test_management_api.exception.ResourceNotFoundException; // Import the new exception


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


//Pagination
 import com.testPortal.test_management_api.common.PageInfo;
 import com.testPortal.test_management_api.common.PagedResponse;
 import org.springframework.data.domain.Page;
 import org.springframework.data.domain.PageRequest;
 import org.springframework.data.domain.Pageable;
 import org.springframework.data.domain.Sort;



@Service
public class ProjectService {

    //1.Inject the repository instead of using a list.
    private final ProjectRepository  projectRepository;

    public ProjectService(ProjectRepository projectRepository){
        this.projectRepository=projectRepository;
    }

    // NOTE: The hardcoded list, the AtomicInteger, and the constructor are all GONE!

    //2.The findAll method is now a one-liner
//    public List<ProjectResponse> findAll(){
//        return projectRepository.findAll() //this gets all Project ENTITIES from the DB
//                .stream()
//                .map(this::convertToResponse) // Convert each Entity to a Response DTO
//                .collect(Collectors.toList());
//    }

//    with pagination

    public PagedResponse<ProjectResponse> findAll(int page, int size, String sortBy, String sortDir) {
        // 1. Configure Sort
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // 2. Create Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Fetch from Repository (This returns the raw Entity Page)
        Page<Project> projectsPage = projectRepository.findAll(pageable);

        // 4. Convert Entities to DTOs
        List<ProjectResponse> content = projectsPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 5. Create our Custom PageInfo
        PageInfo pageInfo = new PageInfo(
                projectsPage.getNumber(),
                projectsPage.getTotalPages(),
                projectsPage.getTotalElements(),
                projectsPage.getSize()
        );

        // 6. Return the combined response
        return new PagedResponse<>(content, pageInfo);
    }
    //3.The findById
    public ProjectResponse findById(Integer id){
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return convertToResponse(project);
    }


    //4. Create
    public ProjectResponse create(CreateProjectRequest request){
        Project project = convertToEntity(request); // conver the request DTO to entity
        Project saveProject = projectRepository.save(project); //save it ti DB
        return convertToResponse(saveProject); //convert the saved entity back to response entity
    }

    //5. Update
    public ProjectResponse update(Integer id, CreateProjectRequest request){
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        existingProject.setName(request.getName());
        existingProject.setDescription(request.getDescription());
        Project updatedProject = projectRepository.save(existingProject);
        return convertToResponse(updatedProject);
    }


    //6. Delete
    public void delete(Integer id){
        if(!projectRepository.existsById(id)){
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }










    // --- Helper Methods for DTO/Entity Conversion ---

    private ProjectResponse convertToResponse(Project project) {
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
    }

    private Project convertToEntity(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return project;
    }

}

























