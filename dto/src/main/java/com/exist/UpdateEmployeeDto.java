package com.exist;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateEmployeeDto {
    private String name;
    private String email; // Allow email update
    private String address;
    private int age;
    private LocalDate birthdate;
    private String contactNumber;
    private EmploymentStatus employmentStatus;
    private AccessLevel accessLevel;
    private String roleName; // Allow role update

}

