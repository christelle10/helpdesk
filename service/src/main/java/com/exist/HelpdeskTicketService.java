package com.exist;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.List;


@Service
public class HelpdeskTicketService {
    private final HelpdeskTicketRepository ticketRepository;
    private final EmployeeRepository employeeRepository;
    private final HelpdeskTicketMapper ticketMapper;

    public HelpdeskTicketService( //did not use the lombok constructor because it cannot resolve the one that needs Qualifier
            HelpdeskTicketRepository ticketRepository,
            RemarkRepository remarkRepository,
            EmployeeRepository employeeRepository,
            @Qualifier("helpdeskTicketMapperImpl") HelpdeskTicketMapper ticketMapper) { // to specify that the helpdeskTicketMapper should be the one to use and not the helpdeskTicketMapperImpl
        this.ticketRepository = ticketRepository;
        this.employeeRepository = employeeRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional
    public HelpdeskTicketDto createTicket(HelpdeskTicketDto dto) {
        HelpdeskTicket ticket = ticketMapper.toEntity(dto); // Map DTO to Entity
        ticket.onCreate(); // Auto-set createdDate, createdBy, and ticketNumber

        // Handle assigned employee
        if (dto.getAssignedEmployeeName() != null && !dto.getAssignedEmployeeName().isEmpty()) {
            Employee assignedEmployee = employeeRepository.findByNameAndDeletedFalse(dto.getAssignedEmployeeName())
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found or is deleted: " + dto.getAssignedEmployeeName()));
            ticket.setAssignedEmployee(assignedEmployee);
        } else {
            ticket.setAssignedEmployee(null);  // Ensure it's null so "Unassigned" is set in DTO mapping
        }

        // **Handle remarks before saving the ticket**
        if (dto.getRemarks() != null && !dto.getRemarks().isEmpty()) {
            List<Remark> remarks = dto.getRemarks().stream()
                    .map(ticketMapper::toEntity)  // Map each RemarkDto to Remark entity
                    .peek(remark -> {
                        remark.setCreatedBy(remark.getCreatedBy() != null ? remark.getCreatedBy() : "System");
                        remark.setCreatedDate(LocalDateTime.now());
                        remark.setTicket(ticket);
                    })
                    .collect(Collectors.toList());
            ticket.setRemarks(remarks);
            Remark latestRemark = remarks.getLast();
            ticket.setUpdatedBy(latestRemark.getCreatedBy());
            ticket.setUpdatedDate(latestRemark.getCreatedDate());
        } else {
            ticket.setUpdatedBy("System");
            ticket.setUpdatedDate(ticket.getCreatedDate());
        }

        // **Save ticket (remarks will be saved automatically due to cascade = ALL)**
        HelpdeskTicket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(savedTicket);  // Use mapper to convert entity to DTO
    }


    public HelpdeskTicketDto getTicket(Long ticketId) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
        return ticketMapper.toDto(ticket);
    }

    public List<HelpdeskTicketDto> getAllTickets() {
        return ticketMapper.toDtoList(ticketRepository.findAll());
    }

    // Status based query methods
    public List<HelpdeskTicketDto> getDraftTickets() {
        return ticketMapper.toDtoList(ticketRepository.findByStatus(HelpdeskTicket.Status.DRAFT));
    }

    public List<HelpdeskTicketDto> getFiledTickets() {
        return ticketMapper.toDtoList(ticketRepository.findByStatus(HelpdeskTicket.Status.FILED));
    }

    public List<HelpdeskTicketDto> getInProgressTickets() {
        return ticketMapper.toDtoList(ticketRepository.findByStatus(HelpdeskTicket.Status.IN_PROGRESS));
    }

    public List<HelpdeskTicketDto> getClosedTickets() {
        return ticketMapper.toDtoList(ticketRepository.findByStatus(HelpdeskTicket.Status.CLOSED));
    }

    public List<HelpdeskTicketDto> getDuplicateTickets() {
        return ticketMapper.toDtoList(ticketRepository.findByStatus(HelpdeskTicket.Status.DUPLICATE));
    }

    // Get Assigned Tickets
    public List<HelpdeskTicketDto> getAssignedTickets() {
        return ticketMapper.toDtoList(ticketRepository.findByAssignedEmployeeIsNotNull());
    }

    @Transactional
    public HelpdeskTicketDto updateTicket(Long ticketId, HelpdeskTicketDto dto) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        // Get the currently authenticated employee's name and role
        String currentUser = JwtAuthenticationUtil.getAuthenticatedEmployeeName();
        String currentUserRole = JwtAuthenticationUtil.getAuthenticatedEmployeeRole(); // Assume this method exists

        // Allow admins to update any ticket
        if (!"ADMIN".equals(currentUserRole)) {
            // Ensure only the assigned employee can update
            if (ticket.getAssignedEmployee() == null ||
                    !ticket.getAssignedEmployee().getName().equals(currentUser)) {
                throw new UnauthorizedAccessException("You are not authorized to update this ticket.");
            }
        }

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
        return ticketMapper.toDto(updatedTicket);
    }


    @Transactional
    public void deleteTicket(Long ticketId) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        // Get the currently authenticated employee's name and role
        String currentUser = JwtAuthenticationUtil.getAuthenticatedEmployeeName();
        String currentUserRole = JwtAuthenticationUtil.getAuthenticatedEmployeeRole(); // Assume this method exists

        // Allow admins to delete any ticket
        if (!"ADMIN".equals(currentUserRole)) {
            // Ensure only the assigned employee can delete
            if (ticket.getAssignedEmployee() == null ||
                    !ticket.getAssignedEmployee().getName().equals(currentUser)) {
                throw new UnauthorizedAccessException("You are not authorized to delete this ticket.");
            }
        }
        ticketRepository.delete(ticket);
    }

    public List<HelpdeskTicketDto> getAssignedTicketsByEmployeeId(Long employeeId) {
        return ticketMapper.toDtoList(ticketRepository.findByAssignedEmployeeId(employeeId));
    }

}
