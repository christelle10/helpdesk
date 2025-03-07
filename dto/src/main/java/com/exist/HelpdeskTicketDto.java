package com.exist;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpdeskTicketDto {
    private Long id;
    private String ticketNumber;
    private String title;
    private String body;
    private String status;
    private String assignedEmployeeName; // Employee's name instead of the whole entity
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private List<RemarkDto> remarks; // List of remarks as DTOs
}
