package com.testPortal.test_management_api.administration;

import com.testPortal.test_management_api.administration.dto.CreateRoleRequest;
import com.testPortal.test_management_api.administration.dto.RoleDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService){
        this.roleService=roleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleDto createRole(@Valid @RequestBody CreateRoleRequest roleRequest){
        return roleService.createRoleDto(roleRequest);
    }

    @GetMapping
    public List<RoleDto> getAllRole(){
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public RoleDto getRoleById(@PathVariable Integer id){
        return roleService.getRoleById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoleById(@PathVariable Integer id){
        roleService.deleteRole(id);
        return ResponseEntity.ok("Role deleted successfully.");

    }
    @PutMapping("/{id}")
    public RoleDto updateRole(@PathVariable Integer id, @Valid @RequestBody CreateRoleRequest request) {
        return roleService.updateRole(id, request);
    }
}
