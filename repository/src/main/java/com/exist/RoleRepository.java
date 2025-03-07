package com.exist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // You can define custom queries here if needed, e.g., findByName, etc.

    // Example of a custom query method to find a role by its name
    Role findByRoleName(String roleName);
}
