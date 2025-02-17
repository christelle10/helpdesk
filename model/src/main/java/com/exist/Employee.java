package com.exist;

import jakarta.persistence.*;
import lombok.*;

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

    private String name;
    private int age;
    private String address;
    private String contactNumber;
    private String employmentStatus;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}