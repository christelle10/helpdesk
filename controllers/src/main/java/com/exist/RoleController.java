package com.exist;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    // Create a new role
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(@Valid @RequestBody RoleDto roleDto) {
        RoleDto savedRole = roleService.saveRole(roleDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Role successfully created.");
        response.put("role", savedRole); // Include the created role details

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all roles
    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    // Get role by ID
    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        RoleDto roleDto = roleService.getRoleById(id); // This method throws RoleNotFoundException if not found
        return ResponseEntity.ok(roleDto);
    }


    // Update role
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDto updatedRoleDto) {
        RoleDto updatedRole = roleService.updateRole(id, updatedRoleDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Role successfully updated.");
        response.put("role", updatedRole); // Include the updated role details

        return ResponseEntity.ok(response);
    }


    // Delete role
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRole(@PathVariable Long id) {
        String message = roleService.deleteRole(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.ok(response);
    }
}
