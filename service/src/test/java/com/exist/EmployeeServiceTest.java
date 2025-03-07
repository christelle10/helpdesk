package com.exist;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Creating new employee
    @Test
    void saveEmployee_ShouldSaveAndReturnEmployeeDto() {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(1L);
        employeeDto.setName("John Doe");
        employeeDto.setBirthdate(LocalDate.of(1990, 5, 20));
        employeeDto.setAddress("123 Street");
        employeeDto.setContactNumber("+1234567890");
        employeeDto.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employeeDto.setRoleName("HR Manager");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setBirthdate(LocalDate.of(1990, 5, 20));
        employee.setAddress("123 Street");
        employee.setContactNumber("+1234567890");
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);

        Role role = new Role();
        role.setRoleName("HR Manager");

        when(roleRepository.findByRoleName("HR Manager")).thenReturn(role);
        employee.setRole(role);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        EmployeeDto savedEmployee = employeeService.saveEmployee(employeeDto);

        // Assert
        assertNotNull(savedEmployee);
        assertEquals("John Doe", savedEmployee.getName());
        assertEquals("HR Manager", savedEmployee.getRoleName());

        // Verify repository interaction
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    //Searching for employee by ID (When employee exists)
    @Test
    void getEmployeeById_ShouldReturnEmployeeDto_WhenEmployeeExists() {
        // Arrange
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setName("John Doe");
        employee.setBirthdate(LocalDate.of(1990, 5, 20));
        employee.setAddress("123 Street");
        employee.setContactNumber("+1234567890");
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeDto result = employeeService.getEmployeeById(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result.getId());
        assertEquals("John Doe", result.getName());

        // Verify repository interaction
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    //Searching for employee by ID (When employee does not exist)
    @Test
    void getEmployeeById_ShouldThrowException_WhenEmployeeNotFound() {
        // Arrange
        Long employeeId = 999L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(employeeId)
        );

        assertEquals("Employee with ID 999 not found.", exception.getMessage());

        // Verify repository interaction
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    //Viewing list of all employees
    @Test
    void getAllEmployees_ShouldReturnListOfEmployeeDtos() {
        // Arrange
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("Alice Smith");
        employee1.setBirthdate(LocalDate.of(1985, 3, 15));
        employee1.setAddress("456 Elm St");
        employee1.setContactNumber("+1122334455");
        employee1.setEmploymentStatus(EmploymentStatus.ACTIVE);

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("Bob Johnson");
        employee2.setBirthdate(LocalDate.of(1992, 7, 10));
        employee2.setAddress("789 Maple Ave");
        employee2.setContactNumber("+5566778899");
        employee2.setEmploymentStatus(EmploymentStatus.INACTIVE);

        List<Employee> employeeList = List.of(employee1, employee2);
        when(employeeRepository.findAll()).thenReturn(employeeList);

        // Act
        List<EmployeeDto> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Alice Smith", result.get(0).getName());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Bob Johnson", result.get(1).getName());

        // Verify repository interaction
        verify(employeeRepository, times(1)).findAll();
    }

    //Full employee record update (put)
    @Test
    void updateEmployee_ShouldPerformFullUpdate_WhenIsFullUpdateIsTrue() {
        // Arrange
        Long employeeId = 1L;
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setName("Updated Name");
        employeeDto.setBirthdate(LocalDate.of(1995, 4, 25));
        employeeDto.setAddress("Updated Address");
        employeeDto.setContactNumber("+9876543210");
        employeeDto.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employeeDto.setRoleName("HR Manager");

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setName("Old Name");
        existingEmployee.setBirthdate(LocalDate.of(1990, 5, 20));
        existingEmployee.setAddress("Old Address");
        existingEmployee.setContactNumber("+1234567890");
        existingEmployee.setEmploymentStatus(EmploymentStatus.INACTIVE);

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(employeeId);
        updatedEmployee.setName("Updated Name");
        updatedEmployee.setBirthdate(LocalDate.of(1995, 4, 25));
        updatedEmployee.setAddress("Updated Address");
        updatedEmployee.setContactNumber("+9876543210");
        updatedEmployee.setEmploymentStatus(EmploymentStatus.ACTIVE);

        Role hrManagerRole = new Role();
        hrManagerRole.setRoleName("HR Manager");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(roleRepository.findByRoleName("HR Manager")).thenReturn(hrManagerRole);
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // Act
        EmployeeDto result = employeeService.updateEmployee(employeeId, employeeDto, true);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Address", result.getAddress());
        assertEquals("+9876543210", result.getContactNumber());
        assertEquals(EmploymentStatus.ACTIVE, result.getEmploymentStatus());

        // Verify repository interactions
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    //Partial employee record update
    @Test
    void updateEmployee_ShouldPerformPartialUpdate_WhenIsFullUpdateIsFalse() {
        // Arrange
        Long employeeId = 1L;
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setAddress("New Partial Address");

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setName("Old Name");
        existingEmployee.setBirthdate(LocalDate.of(1990, 5, 20));
        existingEmployee.setAddress("Old Address");
        existingEmployee.setContactNumber("+1234567890");
        existingEmployee.setEmploymentStatus(EmploymentStatus.ACTIVE);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(existingEmployee);

        // Act
        EmployeeDto result = employeeService.updateEmployee(employeeId, employeeDto, false);

        // Assert
        assertNotNull(result);
        assertEquals("Old Name", result.getName()); // Name should remain unchanged
        assertEquals("New Partial Address", result.getAddress()); // Address should be updated
        assertEquals("+1234567890", result.getContactNumber()); // Contact number should remain unchanged

        // Verify repository interactions
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).save(existingEmployee);
    }

    //Trying to update an employee that does not exist
    @Test
    void updateEmployee_ShouldThrowException_WhenEmployeeNotFound() {
        // Arrange
        Long employeeId = 999L;
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setName("Nonexistent Employee");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(employeeId, employeeDto, true)
        );

        assertEquals("Employee with ID 999 not found.", exception.getMessage());

        // Verify repository interactions
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    //Trying to update age (not allowed)
    @Test
    void updateEmployee_ShouldThrowException_WhenTryingToUpdateAge() {
        // Arrange
        Long employeeId = 1L;
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setAge(30); // This should trigger an exception

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.updateEmployee(employeeId, employeeDto, true)
        );

        assertEquals("Age cannot be updated directly. Update birthdate instead, and age will be calculated automatically.", exception.getMessage());

        // Verify that the repository is never called
        verify(employeeRepository, never()).findById(anyLong());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    //Deleting an employee
    @Test
    void deleteEmployee_ShouldDeleteSuccessfully_WhenEmployeeExists() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.existsById(employeeId)).thenReturn(true);

        // Act
        String result = employeeService.deleteEmployee(employeeId);

        // Assert
        assertEquals("Successfully deleted employee with ID " + employeeId, result);

        // Verify repository interactions
        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(employeeRepository, times(1)).deleteById(employeeId);
    }

    //Deleting an employee that doesn't exist
    @Test
    void deleteEmployee_ShouldThrowException_WhenEmployeeNotFound() {
        // Arrange
        Long employeeId = 999L;
        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        // Act & Assert
        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(employeeId)
        );

        assertEquals("Employee with ID 999 not found.", exception.getMessage());

        // Verify repository interactions
        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(employeeRepository, never()).deleteById(anyLong()); // Ensure deleteById is **never** called
    }


}
