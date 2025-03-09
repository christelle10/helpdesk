package com.exist;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/tickets")
@RequiredArgsConstructor
public class HelpdeskTicketController {
    private final HelpdeskTicketService ticketService;

    @PostMapping
    public ResponseEntity<HelpdeskTicketDto> createTicket(@RequestBody HelpdeskTicketDto dto) {
        return ResponseEntity.ok(ticketService.createTicket(dto));
    }


    @GetMapping("/{ticketId}")
    public ResponseEntity<HelpdeskTicketDto> getTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.getTicket(ticketId));
    }

    @GetMapping
    public ResponseEntity<List<HelpdeskTicketDto>> getAllTickets() {
        List<HelpdeskTicketDto> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    // Get Draft Tickets
    @GetMapping("/draft")
    public ResponseEntity<List<HelpdeskTicketDto>> getDraftTickets() {
        return ResponseEntity.ok(ticketService.getDraftTickets());
    }

    // Get Filed Tickets
    @GetMapping("/filed")
    public ResponseEntity<List<HelpdeskTicketDto>> getFiledTickets() {
        return ResponseEntity.ok(ticketService.getFiledTickets());
    }

    // Get In-Progress Tickets
    @GetMapping("/in-progress")
    public ResponseEntity<List<HelpdeskTicketDto>> getInProgressTickets() {
        return ResponseEntity.ok(ticketService.getInProgressTickets());
    }

    // Get Closed Tickets
    @GetMapping("/closed")
    public ResponseEntity<List<HelpdeskTicketDto>> getClosedTickets() {
        return ResponseEntity.ok(ticketService.getClosedTickets());
    }

    // Get Duplicate Tickets
    @GetMapping("/duplicate")
    public ResponseEntity<List<HelpdeskTicketDto>> getDuplicateTickets() {
        return ResponseEntity.ok(ticketService.getDuplicateTickets());
    }

    // Get all assigned tickets
    @GetMapping("/assigned")
    public List<HelpdeskTicketDto> getAssignedTickets() {
        return ticketService.getAssignedTickets();
    }

    // Get Tickets Assigned to Specific Employee by Employee ID
    @GetMapping("/assigned/{employeeId}")
    public ResponseEntity<List<HelpdeskTicketDto>> getAssignedTicketsByEmployeeId(@PathVariable Long employeeId) {
        List<HelpdeskTicketDto> tickets = ticketService.getAssignedTicketsByEmployeeId(employeeId);
        return ResponseEntity.ok(tickets);
    }

    @PatchMapping("/{ticketId}")
    public ResponseEntity<HelpdeskTicketDto> partiallyUpdateTicket(
            @PathVariable Long ticketId,
            @RequestBody HelpdeskTicketDto dto) {
        return ResponseEntity.ok(ticketService.updateTicket(ticketId, dto));
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<HelpdeskTicketDto> updateTicket(@PathVariable Long ticketId, @RequestBody HelpdeskTicketDto dto) {
        return ResponseEntity.ok(ticketService.updateTicket(ticketId, dto));
    }

    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<HelpdeskTicketDto> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestBody Map<String, String> payload) {

        String status = payload.get("status");
        return ResponseEntity.ok(ticketService.updateTicketStatus(ticketId, status));
    }


    // DELETE Ticket by ID
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<String> deleteTicket(@PathVariable Long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.ok("Ticket with ID " + ticketId + " has been deleted successfully.");
    }
}
