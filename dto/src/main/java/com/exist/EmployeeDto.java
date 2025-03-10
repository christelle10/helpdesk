package com.exist;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDto {
    private Long id;
    private String name;
    private LocalDate birthdate;
    //Age calculated from birthdate
    private int age;
    private String address;
    private String contactNumber;
    private EmploymentStatus employmentStatus;
    private String roleName; // Only the role name instead of the full Role entity

    // Soft delete info (use Boolean instead of boolean to allow null)
    @JsonProperty("is_deleted")
    private Boolean deleted;
    private LocalDateTime deletedAt;

    private AccessLevel accessLevel; // Include access level in the DTO
}
