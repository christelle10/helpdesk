package com.exist;

import com.exist.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private RoleService roleService;

    private Employee createEmployee(Long id, String name, LocalDate birthdate, String address, String contactNumber, EmploymentStatus status, Role role) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setBirthdate(birthdate);
        employee.setAddress(address);
        employee.setContactNumber(contactNumber);
        employee.setEmploymentStatus(status);
        employee.setRole(role);
        return employee;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    //Creating a new role
    @Test
    void saveRole_ShouldReturnSavedRoleDto() {
        // Arrange
        RoleDto roleDto = new RoleDto(1L, "Admin");
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("Admin");

        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // Act
        RoleDto savedRoleDto = roleService.saveRole(roleDto);

        // Assert
        assertEquals(roleDto.getId(), savedRoleDto.getId());
        assertEquals(roleDto.getRoleName(), savedRoleDto.getRoleName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    //Searching for role by Id
    @Test
    void getRoleById_ShouldReturnRoleDto_WhenRoleExists() {
        // Arrange
        Long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);
        role.setRoleName("HR Manager");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        // Act
        RoleDto result = roleService.getRoleById(roleId);

        // Assert
        assertNotNull(result);
        assertEquals(roleId, result.getId());
        assertEquals("HR Manager", result.getRoleName());

        // Verify repository interaction
        verify(roleRepository, times(1)).findById(roleId);
    }

    //Searching for role ID but not found
    @Test
    void getRoleById_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        Long roleId = 999L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.getRoleById(roleId)
        );

        assertEquals("Role with ID 999 not found.", exception.getMessage());

        // Verify repository interaction
        verify(roleRepository, times(1)).findById(roleId);
    }

    //Viewing all roles
    @Test
    void getAllRoles_ShouldReturnListOfRoleDtos() {
        // Arrange
        Role role1 = new Role();
        role1.setId(1L);
        role1.setRoleName("HR Manager");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setRoleName("Software Engineer");

        List<Role> roles = List.of(role1, role2);
        when(roleRepository.findAll()).thenReturn(roles);

        // Act
        List<RoleDto> result = roleService.getAllRoles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("HR Manager", result.get(0).getRoleName());
        assertEquals("Software Engineer", result.get(1).getRoleName());

        // Verify repository interaction
        verify(roleRepository, times(1)).findAll();
    }

    //Updating an existing rolename
    @Test
    void updateRole_ShouldUpdateAndReturnUpdatedRoleDto_WhenRoleExists() {
        // Arrange
        Long roleId = 1L;
        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setRoleName("HR Manager");

        RoleDto updatedRoleDto = new RoleDto();
        updatedRoleDto.setRoleName("Senior HR Manager");

        Role updatedRole = new Role();
        updatedRole.setId(roleId);
        updatedRole.setRoleName("Senior HR Manager");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

        // Act
        RoleDto result = roleService.updateRole(roleId, updatedRoleDto);

        // Assert
        assertNotNull(result);
        assertEquals("Senior HR Manager", result.getRoleName());

        // Verify repository interactions
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    //Updating a role that doesn't exist
    @Test
    void updateRole_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        Long roleId = 999L;
        RoleDto updatedRoleDto = new RoleDto();
        updatedRoleDto.setRoleName("Senior HR Manager");

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.updateRole(roleId, updatedRoleDto)
        );

        assertEquals("Role not found.", exception.getMessage());

        // Verify repository interaction
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void deleteRole_ShouldDeleteRole_WhenRoleExistsAndNotAssignedToEmployees() {
        // Arrange
        Long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);
        role.setRoleName("HR Manager");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(employeeRepository.findByRole(role)).thenReturn(Collections.emptyList());

        // Act
        String result = roleService.deleteRole(roleId);

        // Assert
        assertEquals("Role deleted successfully.", result);
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void deleteRole_ShouldNullifyRoleForEmployeesAndDelete_WhenAssignedToEmployees() {
        // Arrange
        Long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);
        role.setRoleName("HR Manager");

        Employee employee = createEmployee(1L, "John Doe", LocalDate.of(1990, 5, 15), "123 Street", "1234567890", EmploymentStatus.ACTIVE, role);
        List<Employee> employees = List.of(employee);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(employeeRepository.findByRole(role)).thenReturn(employees);

        // Act
        String result = roleService.deleteRole(roleId);

        // Assert
        assertEquals("The role is assigned to employees. Deleting it will remove their role.", result);
        assertNull(employee.getRole());
        verify(employeeRepository, times(1)).save(employee);
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void deleteRole_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        Long roleId = 999L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.deleteRole(roleId)
        );

        assertEquals("Role not found.", exception.getMessage());
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).delete(any(Role.class));
    }
}
