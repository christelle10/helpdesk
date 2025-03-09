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
    private final HelpdeskTicketMapper ticketMapper;

    @Transactional
    public HelpdeskTicketDto createTicket(HelpdeskTicketDto dto) {
        HelpdeskTicket ticket = ticketMapper.toEntity(dto); // Map DTO to Entity
        /*ticket.setTitle(dto.getTitle());
        ticket.setBody(dto.getBody());
        ticket.setStatus(HelpdeskTicket.Status.valueOf(dto.getStatus()));*/
        ticket.onCreate(); // Auto-set createdDate, createdBy, and ticketNumber

        // Handle assigned employee
        if (dto.getAssignedEmployeeName() != null && !dto.getAssignedEmployeeName().isEmpty()) {
            Employee assignedEmployee = employeeRepository.findByName(dto.getAssignedEmployeeName())
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + dto.getAssignedEmployeeName()));
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
            /*for (RemarkDto remarkDto : dto.getRemarks()) {
                Remark remark = new Remark();
                remark.setMessage(remarkDto.getMessage());
                remark.setCreatedBy(remarkDto.getCreatedBy() != null ? remarkDto.getCreatedBy() : "System");
                remark.setCreatedDate(LocalDateTime.now());
                remark.setTicket(ticket);  // Link remark to ticket
                ticket.getRemarks().add(remark);  // ✅ Add remark to ticket's list
            }*/
            // **Update ticket's updatedBy and updatedDate based on the latest remark**
            Remark latestRemark = remarks.get(remarks.size() - 1);
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
    public HelpdeskTicketDto updateTicketStatus(Long ticketId, String status) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        ticket.setStatus(HelpdeskTicket.Status.valueOf(status.toUpperCase()));
        ticket.onUpdate();

        HelpdeskTicket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Transactional
    public void deleteTicket(Long ticketId) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
        ticketRepository.delete(ticket);
    }




    /* private mappers
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
    }*/


    public List<HelpdeskTicketDto> getAssignedTicketsByEmployeeId(Long employeeId) {
        return ticketMapper.toDtoList(ticketRepository.findByAssignedEmployeeId(employeeId));
    }

}
