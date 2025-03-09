package com.exist;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String contactNumber;

    @Enumerated(EnumType.STRING) // Store enum values as Strings in the DB
    @Column(nullable = false)
    private EmploymentStatus employmentStatus;

    @ManyToOne //Many to Many
    @JoinColumn(name = "role_id")
    private Role role; //Deleting a Role entity associated to Employee entity will render this NULL

    // Soft delete field
    @Column(name = "is_deleted", nullable = false)
    @JsonProperty("is_deleted")
    private boolean deleted = false;  // Default false

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Optional: Track when it was deleted

    @Transactional
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        this.age = DateUtils.calculateAge(birthdate); // Use utility method
    }

    @PrePersist
    @PreUpdate
    public void updateAgeBeforeSave() {
        this.age = DateUtils.calculateAge(this.birthdate); // Use utility method
    }
    public void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }


}