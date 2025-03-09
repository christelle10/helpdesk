package com.exist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional
    // Create or update an employee record as DTO
    public EmployeeDto saveEmployee(EmployeeDto employeeDTO) {
        Employee employee = employeeMapper.toEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee {} saved successfully with ID: {}", employeeDTO.getName(), savedEmployee.getId());
        return employeeMapper.toDto(savedEmployee);
    }

    // Get a single employee by ID as DTO
    public EmployeeDto getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Employee with ID {} not found", id);
                    return new EmployeeNotFoundException("Employee with ID " + id + " not found.");
                });
    }

    // Get a list of all employees as DTOs
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findByDeletedFalse(); //Modified this one so we only fetch active employees
        log.info("Total active employees found: {}", employees.size());
        return employees.stream().map(employeeMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDTO, boolean isFullUpdate) {
        log.info("Updating employee with ID: {}", id);
        if (employeeDTO.getAge() != 0) {
            log.warn("Age cannot be updated directly.");
            throw new IllegalArgumentException("Age cannot be updated directly. Update birthdate instead, and age will be calculated automatically.");
        }

        return employeeRepository.findById(id)
                .map(existingEmployee -> {
                    if (isFullUpdate) {
                        // Full update: Replace all fields
                        log.info("Performing full update on employee with ID: {}", id);
                        validateEmployee(employeeDTO);
                        Employee updatedEmployee = employeeMapper.toEntity(employeeDTO);
                        updatedEmployee.setId(id); // Ensure ID remains unchanged
                        return employeeRepository.save(updatedEmployee);
                    } else {
                        // Partial update: Only copy non-null fields
                        log.info("Performing partial update on employee with ID: {}", id);
                        validatePartialUpdate(employeeDTO);
                        copyNonNullProperties(employeeDTO, existingEmployee);
                        return employeeRepository.save(existingEmployee);
                    }
                })
                .map(employeeMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Employee with ID {} not found for update", id);
                    return new EmployeeNotFoundException("Employee with ID " + id + " not found.");
                });
    }
    //Soft Delete
    @Transactional
    public String deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + id + " not found."));

        employee.softDelete();
        employeeRepository.save(employee);

        log.info("Employee with ID {} was soft deleted.", id);
        return "Employee successfully soft deleted.";
    }

    //Viewing a deleted employee
    public List<EmployeeDto> getDeletedEmployees() {
        List<Employee> deletedEmployees = employeeRepository.findByDeletedTrue();
        return deletedEmployees.stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    // Restoring deleted employee
    @Transactional
    public String restoreEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + id + " not found."));

        if (!employee.isDeleted()) {
            return "Employee is already active.";
        }
        employee.setDeleted(false);
        employee.setDeletedAt(null);
        employeeRepository.save(employee);
        log.info("Employee with ID {} was restored.", id);
        return "Employee restored successfully.";
    }


    // Utility method to copy only non-null properties
    private void copyNonNullProperties(EmployeeDto source, Employee target) {
        // Ensure null properties are ignored
        String[] ignoredProperties = getNullPropertyNames(source);
        BeanUtils.copyProperties(source, target, ignoredProperties);

        // Manually handle Role (because it's a separate entity)
        if (source.getRoleName() != null) {
            Role role = roleRepository.findByRoleName(source.getRoleName());
            target.setRole(role);
        }
    }


    // Get null property names for ignoring
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors()) // Get all properties
                .map(PropertyDescriptor::getName) // Extract property name
                .filter(name -> src.getPropertyValue(name) == null) // Check if value is null
                .toArray(String[]::new); // Convert to array
    }
    public void validateEmployee(EmployeeDto employeeDto) {
        Map<String, String> errors = new HashMap<>();

        if (employeeDto.getName() == null || employeeDto.getName().isBlank()) {
            errors.put("name", "Employee name cannot be empty.");
        }
        if (employeeDto.getBirthdate() == null || employeeDto.getBirthdate().isAfter(LocalDate.now())) {
            errors.put("birthdate", "Birthdate must be in the past.");
        }
        if (employeeDto.getAddress() == null || employeeDto.getAddress().isBlank()) {
            errors.put("address", "Address cannot be empty.");
        }
        if (employeeDto.getContactNumber() == null || !employeeDto.getContactNumber().matches("^\\+?[0-9]{7,15}$")) {
            errors.put("contactNumber", "Contact number must be 7 to 15 digits long.");
        }
        if (employeeDto.getRoleName() != null && roleRepository.findByRoleName(employeeDto.getRoleName()) == null) {
            errors.put("roleName", "Invalid role name.");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }
    }
    public void validatePartialUpdate(EmployeeDto employeeDto) {
        Map<String, String> errors = new HashMap<>();
        // Validate Name - Must not contain symbols, only letters and spaces
        if (employeeDto.getName() != null) {
            if (employeeDto.getName().isBlank()) {
                errors.put("name", "Employee name cannot be empty.");
        }
            // Validate Birthdate - Must be in the past
            if (employeeDto.getBirthdate() != null) {
                if (employeeDto.getBirthdate().isAfter(LocalDate.now())) {
                    errors.put("birthdate", "Birthdate must be in the past.");
                }
            }

            // Validate Address - Must not be empty if provided
            if (employeeDto.getAddress() != null && employeeDto.getAddress().isBlank()) {
                errors.put("address", "Address cannot be empty.");
            }

            // Validate Contact Number - Must follow the correct pattern if provided
            if (employeeDto.getContactNumber() != null) {
                if (!employeeDto.getContactNumber().matches("^\\+?[0-9]{7,15}$")) {
                    errors.put("contactNumber", "Contact number must be 7 to 15 digits long and can optionally start with '+'. Example: +1234567890 or 1234567890.");
                }
            }

            // Validate Employment Status - Ignore if null (PATCH allows partial updates)
            if (employeeDto.getEmploymentStatus() != null) {
                boolean isValidEnum = Arrays.stream(EmploymentStatus.values())
                        .anyMatch(status -> status == employeeDto.getEmploymentStatus());
                if (!isValidEnum) {
                    errors.put("employmentStatus", "Invalid employment status.");
                }
            }

            // Validate Role - Ensure the roleName exists in the database if provided
            if (employeeDto.getRoleName() != null) {
                Role role = roleRepository.findByRoleName(employeeDto.getRoleName());
                if (role == null) {
                    errors.put("roleName", "Role '" + employeeDto.getRoleName() + "' does not exist. Please provide a valid role.");
                }
            }

            // If there are errors, throw an exception with structured error response
            if (!errors.isEmpty()) {
                throw new IllegalArgumentException(errors.toString());
            }
        }

    }
}



