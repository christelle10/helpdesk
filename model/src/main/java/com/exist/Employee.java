package com.exist;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


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

    @Column(nullable = false)
    private String employmentStatus;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role; //Deleting a Role entity associated to Employee entity will render this NULL

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        this.age = DateUtils.calculateAge(birthdate); // Use utility method
    }

    @PrePersist
    @PreUpdate
    public void updateAgeBeforeSave() {
        this.age = DateUtils.calculateAge(this.birthdate); // Use utility method
    }
}