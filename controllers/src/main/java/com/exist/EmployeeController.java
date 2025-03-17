package com.exist;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin/employees")
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

    //create new employee
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEmployee(@Valid @RequestBody RegisterEmployeeDto registerEmployeeDto) {
        EmployeeDto savedEmployee = employeeService.saveEmployee(registerEmployeeDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee successfully created.");
        response.put("employee", savedEmployee);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Full Update (PUT) - Updates all fields
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeDto employeeDTO) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDTO, true);
        return ResponseEntity.ok(updatedEmployee);
    }


    // Partially update an employee (PATCH method)
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> patchEmployee(
            @PathVariable Long id,
            //@Validated(PartialUpdate.class)
            @RequestBody UpdateEmployeeDto updateEmployeeDto) {
        try {
            employeeService.validatePartialUpdate(updateEmployeeDto);
            EmployeeDto updatedEmployee = employeeService.updateEmployee(id, updateEmployeeDto, false);
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

    //Viewing deleted employees
    @GetMapping("/deleted")
    public List<EmployeeDto> getDeletedEmployees() {
        return employeeService.getDeletedEmployees();
    }

    // Restoring deleted employee records
    @PutMapping("/{id}/restore")
    public ResponseEntity<String> restoreEmployee(@PathVariable Long id) {
        String message = employeeService.restoreEmployee(id);
        return ResponseEntity.ok(message);
    }

}
