package com.exist;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;
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
}
