package com.exist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RemarkService {

    private final RemarkRepository remarkRepository;
    private final HelpdeskTicketRepository ticketRepository;

    public List<RemarkDto> getRemarksByTicketId(Long ticketId) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        return ticket.getRemarks().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private RemarkDto mapToDto(Remark remark) {
        return RemarkDto.builder()
                .id(remark.getId())
                .message(remark.getMessage())
                .createdDate(remark.getCreatedDate())
                .createdBy(remark.getCreatedBy())
                .build();
    }
    public RemarkDto addRemark(Long ticketId, RemarkDto remarkDto) {
        // 1️⃣ Find the ticket
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        // 2️⃣ Create new Remark
        Remark remark = new Remark();
        remark.setMessage(remarkDto.getMessage());
        remark.setCreatedBy(remarkDto.getCreatedBy() != null ? remarkDto.getCreatedBy() : "System");
        remark.setCreatedDate(LocalDateTime.now());
        remark.setTicket(ticket);

        // 3️⃣ Save the remark
        Remark savedRemark = remarkRepository.save(remark);

        // 4️⃣ Update ticket's `updatedBy` and `updatedDate`
        ticket.setUpdatedBy(savedRemark.getCreatedBy());
        ticket.setUpdatedDate(savedRemark.getCreatedDate());
        ticketRepository.save(ticket);

        // 5️⃣ Return DTO
        return new RemarkDto(
                savedRemark.getId(),
                savedRemark.getMessage(),
                savedRemark.getCreatedDate(),
                savedRemark.getCreatedBy()
        );
    }

    public void deleteRemark(Long ticketId, Long remarkId) {
        Remark remark = remarkRepository.findById(remarkId)
                .orElseThrow(() -> new ResourceNotFoundException("Remark not found with ID: " + remarkId));

        // Ensure the remark belongs to the ticket
        if (!remark.getTicket().getId().equals(ticketId)) {
            throw new IllegalArgumentException("Remark does not belong to the specified ticket");
        }

        remarkRepository.delete(remark);
    }
}
