package com.exist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByName(String name);
    List<Employee> findByRole(Role role);

    // Exclude soft-deleted records
    List<Employee> findByDeletedFalse();

    // Find by ID but exclude deleted ones
    Optional<Employee> findByIdAndDeletedFalse(Long id);

    // Custom JPQL query to exclude soft-deleted employees
    @Query("SELECT e FROM Employee e WHERE e.deleted = false")  // Use entity field "deleted"
    List<Employee> findActiveEmployees();

    List<Employee> findByDeletedTrue();

    // Restore a deleted employee using native SQL (because JPQL doesn't support updates)
    @Query(value = "UPDATE employees SET is_deleted = false, deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restoreEmployee(@Param("id") Long id);
}