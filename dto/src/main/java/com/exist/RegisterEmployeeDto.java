package com.exist;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterEmployeeDto {
    private String name;
    private String email;
    private String address;
    private LocalDate birthdate;
    private String contactNumber;
    private EmploymentStatus employmentStatus;
    private AccessLevel accessLevel; // ADMIN or EMPLOYEE

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Prevent exposure in responses
    private String password;
}