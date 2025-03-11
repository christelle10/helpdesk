package com.exist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    @Modifying // Required for UPDATE/DELETE queries
    @Transactional // Ensures the update is executed within a transaction
    @Query(value = "UPDATE employees SET is_deleted = false, deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restoreEmployee(@Param("id") Long id);

    // Check if an active (non-deleted) employee exists with the same name or contact number
    @Query("SELECT e FROM Employee e WHERE (e.name = :name OR e.contactNumber = :contactNumber) AND e.deleted = false")
    Optional<Employee> findExistingEmployee(@Param("name") String name, @Param("contactNumber") String contactNumber);
    Optional<Employee> findByEmail(String email);

}