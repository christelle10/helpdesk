package com.exist;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.List;


@Service
@RequiredArgsConstructor
public class HelpdeskTicketService {
    private final HelpdeskTicketRepository ticketRepository;
    private final RemarkRepository remarkRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public HelpdeskTicketDto createTicket(HelpdeskTicketDto dto) {
        HelpdeskTicket ticket = new HelpdeskTicket();
        ticket.setTitle(dto.getTitle());
        ticket.setBody(dto.getBody());
        ticket.setStatus(HelpdeskTicket.Status.valueOf(dto.getStatus()));
        ticket.onCreate(); // Auto-set createdDate, createdBy, and ticketNumber

        // **Handle assigned employee**
        // **Handle assigned employee**
        if (dto.getAssignedEmployeeName() != null && !dto.getAssignedEmployeeName().isEmpty()) {
            Employee assignedEmployee = employeeRepository.findByName(dto.getAssignedEmployeeName())
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + dto.getAssignedEmployeeName()));
            ticket.setAssignedEmployee(assignedEmployee);
        } else {
            ticket.setAssignedEmployee(null);  // Ensure it's null so "Unassigned" is set in DTO mapping
        }

        // **Handle remarks before saving the ticket**
        if (dto.getRemarks() != null && !dto.getRemarks().isEmpty()) {
            for (RemarkDto remarkDto : dto.getRemarks()) {
                Remark remark = new Remark();
                remark.setMessage(remarkDto.getMessage());
                remark.setCreatedBy(remarkDto.getCreatedBy() != null ? remarkDto.getCreatedBy() : "System");
                remark.setCreatedDate(LocalDateTime.now());
                remark.setTicket(ticket);  // Link remark to ticket
                ticket.getRemarks().add(remark);  // ✅ Add remark to ticket's list
            }
            // **Update ticket's updatedBy and updatedDate based on the latest remark**
            Remark latestRemark = ticket.getRemarks().get(ticket.getRemarks().size() - 1);
            ticket.setUpdatedBy(latestRemark.getCreatedBy());
            ticket.setUpdatedDate(latestRemark.getCreatedDate());
        } else {
            ticket.setUpdatedBy("System");
            ticket.setUpdatedDate(ticket.getCreatedDate());
        }

        // **Save ticket (remarks will be saved automatically due to cascade = ALL)**
        HelpdeskTicket savedTicket = ticketRepository.save(ticket);

        return mapToDto(savedTicket);
    }


    public HelpdeskTicketDto getTicket(Long ticketId) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
        return mapToDto(ticket);
    }

    public List<HelpdeskTicketDto> getAllTickets() {
        return mapToDtoList(ticketRepository.findAll());
    }

    // Get Draft Tickets
    public List<HelpdeskTicketDto> getDraftTickets() {
        return mapToDtoList(ticketRepository.findByStatus(HelpdeskTicket.Status.DRAFT));
    }

    // Get Filed Tickets
    public List<HelpdeskTicketDto> getFiledTickets() {
        return mapToDtoList(ticketRepository.findByStatus(HelpdeskTicket.Status.FILED));
    }

    // Get In-Progress Tickets
    public List<HelpdeskTicketDto> getInProgressTickets() {
        return mapToDtoList(ticketRepository.findByStatus(HelpdeskTicket.Status.IN_PROGRESS));
    }

    // Get Closed Tickets
    public List<HelpdeskTicketDto> getClosedTickets() {
        return mapToDtoList(ticketRepository.findByStatus(HelpdeskTicket.Status.CLOSED));
    }

    // Get Duplicate Tickets
    public List<HelpdeskTicketDto> getDuplicateTickets() {
        return mapToDtoList(ticketRepository.findByStatus(HelpdeskTicket.Status.DUPLICATE));
    }

    // Get Assigned Tickets
    public List<HelpdeskTicketDto> getAssignedTickets() {
        return mapToDtoList(ticketRepository.findByAssignedEmployeeIsNotNull());
    }

    @Transactional
    public HelpdeskTicketDto updateTicket(Long ticketId, HelpdeskTicketDto dto) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        // Only update fields if they are not null
        if (dto.getTitle() != null) {
            ticket.setTitle(dto.getTitle());
        }
        if (dto.getBody() != null) {
            ticket.setBody(dto.getBody());
        }
        if (dto.getStatus() != null) {
            ticket.setStatus(HelpdeskTicket.Status.valueOf(dto.getStatus()));
        }
        if (dto.getAssignedEmployeeName() != null) {
            Employee assignedEmployee = employeeRepository.findByName(dto.getAssignedEmployeeName())
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + dto.getAssignedEmployeeName()));
            ticket.setAssignedEmployee(assignedEmployee);
        }

        // Update timestamp and last modified user
        ticket.onUpdate();

        HelpdeskTicket updatedTicket = ticketRepository.save(ticket);
        return mapToDto(updatedTicket);
    }




    @Transactional
    public HelpdeskTicketDto updateTicketStatus(Long ticketId, String status) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        ticket.setStatus(HelpdeskTicket.Status.valueOf(status.toUpperCase()));
        ticket.onUpdate();

        HelpdeskTicket updatedTicket = ticketRepository.save(ticket);
        return mapToDto(updatedTicket);
    }

    @Transactional
    public void deleteTicket(Long ticketId) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        ticketRepository.delete(ticket);
    }


    /* private mappers */
    private HelpdeskTicketDto mapToDto(HelpdeskTicket ticket) {
        return HelpdeskTicketDto.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .title(ticket.getTitle())
                .body(ticket.getBody())
                .status(ticket.getStatus().name())
                .assignedEmployeeName(ticket.getAssignedEmployee() != null ? ticket.getAssignedEmployee().getName() : "Unassigned")
                .createdDate(ticket.getCreatedDate())
                .createdBy(ticket.getCreatedBy())
                .updatedDate(ticket.getUpdatedDate())
                .updatedBy(ticket.getUpdatedBy())
                .remarks(ticket.getRemarks().stream().map(this::mapToRemarkDto).collect(Collectors.toList()))
                .build();
    }

    private RemarkDto mapToRemarkDto(Remark remark) {
        return RemarkDto.builder()
                .id(remark.getId())
                .message(remark.getMessage())
                .createdDate(remark.getCreatedDate())
                .createdBy(remark.getCreatedBy())
                .build();
    }
    // Helper method to map entity list to DTO list
    private List<HelpdeskTicketDto> mapToDtoList(List<HelpdeskTicket> tickets) {
        return tickets.stream()
                .map(this::mapToDto) // Use the existing mapToDto method
                .collect(Collectors.toList());
    }


}
