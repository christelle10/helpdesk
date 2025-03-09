package com.exist;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface HelpdeskTicketMapper {
    HelpdeskTicketMapper INSTANCE = Mappers.getMapper(HelpdeskTicketMapper.class);

    @Mapping(source = "assignedEmployee.name", target = "assignedEmployeeName", defaultValue = "Unassigned")
    @Mapping(source = "remarks", target = "remarks", qualifiedByName = "mapRemarks")
    HelpdeskTicketDto toDto(HelpdeskTicket ticket);

    @Mapping(target = "id", ignore = true) // Ignore ID to prevent overwriting existing entities
    @Mapping(target = "assignedEmployee", ignore = true) // Handle assignment manually in the service
    @Mapping(target = "remarks", ignore = true) // Remarks are mapped separately
    HelpdeskTicket toEntity(HelpdeskTicketDto dto);

    List<HelpdeskTicketDto> toDtoList(List<HelpdeskTicket> tickets);

    @Named("mapRemarks")
    default List<RemarkDto> mapRemarks(List<Remark> remarks) {
        return remarks.stream()
                .map(this::toRemarkDto)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true) // ID is handled by DB
    Remark toEntity(RemarkDto dto);

    @Mapping(target = "id", source = "id")
    RemarkDto toRemarkDto(Remark remark);
}
