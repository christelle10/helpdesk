package com.exist;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    // Convert Employee -> EmployeeDto
    @Mapping(source = "role.roleName", target = "roleName", defaultValue = "No Role Yet")
    EmployeeDto toDto(Employee employee);

    // Convert EmployeeDto -> Employee
    @Mapping(target = "id", ignore = true) // Ignore ID to avoid accidental overwrites
    @Mapping(target = "role", ignore = true) // We'll handle role manually in service
    Employee toEntity(EmployeeDto dto);
}
