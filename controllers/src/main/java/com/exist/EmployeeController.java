package com.exist;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;


    // Get all employees
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // Get a single employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // Create a new employee
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto savedEmployee = employeeService.saveEmployee(employeeDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee successfully created.");
        response.put("employee", savedEmployee); // Include the created employee details

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Full Update (PUT) - Updates all fields
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeDto employeeDTO) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDTO, true);
        return ResponseEntity.ok(updatedEmployee);
    }


    // Partially update an employee (PATCH method)
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> patchEmployee(
            @PathVariable Long id,
            //@Validated(PartialUpdate.class)
            @RequestBody EmployeeDto employeeDto) {
        try {
            employeeService.validatePartialUpdate(employeeDto);
            EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto, false);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Employee successfully updated.");
            response.put("employee", updatedEmployee); // Include updated details

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Deleting an existing employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Employee with ID " + id + " has been successfully deleted.");
        return ResponseEntity.ok(response);
    }
}
