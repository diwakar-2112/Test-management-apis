package com.testPortal.test_management_api.administration;

import com.testPortal.test_management_api.administration.dto.ModuleDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/admin/modules")
public class ModuleController {
    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService){
        this.moduleService=moduleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModuleDto createModule(@Valid @RequestBody ModuleDto request){
        return moduleService.createModule(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ModuleDto> getAllModules(){
        return moduleService.getAllModules();
    }

}
