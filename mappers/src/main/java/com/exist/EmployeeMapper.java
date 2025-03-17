package com.exist;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    // Convert Employee -> EmployeeDto
    @Mapping(source = "role.roleName", target = "roleName", defaultValue = "No Role Yet")
    @Mapping(source = "accessLevel", target = "accessLevel")
    EmployeeDto toDto(Employee employee);

    // Convert EmployeeDto -> Employee
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true) // Role is handled manually
    @Mapping(source = "accessLevel", target = "accessLevel")
    Employee toEntity(EmployeeDto dto);

    // ✅ New method to convert UpdateEmployeeDto -> Employee
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true) // Role is handled manually
    @Mapping(source = "accessLevel", target = "accessLevel")
    Employee toEntity(UpdateEmployeeDto dto);
}
