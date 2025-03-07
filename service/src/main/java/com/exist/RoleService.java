package com.exist;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;


    // Convert Role entity to DTO
    private RoleDto convertToDTO(Role role) {
        return new RoleDto(role.getId(), role.getRoleName());
    }

    // Convert DTO to Role entity
    private Role convertToEntity(RoleDto roleDto) {
        return new Role(roleDto.getId(), roleDto.getRoleName());
    }

    // Create or update a role
    public RoleDto saveRole(RoleDto roleDto) {
        logger.info("Saving role: {}", roleDto.getRoleName());
        Role role = convertToEntity(roleDto);
        Role savedRole = roleRepository.save(role);
        logger.info("Role saved successfully with ID: {}", savedRole.getId());
        return convertToDTO(savedRole);
    }

    // Get a role by ID
    public RoleDto getRoleById(Long id) {
        logger.info("Fetching role with ID: {}", id);
        return roleRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> {
                    logger.warn("Role with ID {} not found", id);
                    return new RoleNotFoundException("Role with ID " + id + " not found.");
                });
    }

    // List all roles
    public List<RoleDto> getAllRoles() {
        logger.info("Fetching all roles");
        return roleRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Update an existing role
    public RoleDto updateRole(Long id, RoleDto updatedRoleDto) {
        logger.info("Updating role with ID: {}", id);
        return roleRepository.findById(id)
                .map(existingRole -> {
                    existingRole.setRoleName(updatedRoleDto.getRoleName());
                    Role updatedRole = roleRepository.save(existingRole);
                    logger.info("Role updated successfully with new name: {}", updatedRole.getRoleName());
                    return convertToDTO(updatedRole);
                })
                .orElseThrow(() -> {
                    logger.warn("Role with ID {} not found", id);
                    return new RoleNotFoundException("Role not found.");
                });
    }

    // Delete role
    @Transactional
    public String deleteRole(Long id) {
        logger.info("Deleting role with ID: {}", id);
        Optional<Role> roleOptional = roleRepository.findById(id);

        if (roleOptional.isEmpty()) {
            logger.warn("Role with ID {} not found", id);
            throw new RoleNotFoundException("Role not found.");
        }

        Role role = roleOptional.get();
        List<Employee> employeesWithRole = employeeRepository.findByRole(role);
        String message;
        if (!employeesWithRole.isEmpty()) {
            // Inform the user
            message = "The role is assigned to employees. Deleting it will remove their role.";
            logger.warn("Role ID {} is assigned to employees. Nullifying role before deletion.", id);
            // Nullify role for employees before deleting
            for (Employee employee : employeesWithRole) {
                employee.setRole(null);
                employeeRepository.save(employee);
            }
        } else {
            message = "Role deleted successfully.";
        }

        // Now delete the role safely
        roleRepository.delete(role);
        logger.info("Role with ID {} deleted successfully", id);
        return message;
    }
}
