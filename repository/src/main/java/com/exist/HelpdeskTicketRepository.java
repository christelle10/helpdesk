package com.exist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HelpdeskTicketRepository extends JpaRepository<HelpdeskTicket, Long> {
    // Find tickets by status
    List<HelpdeskTicket> findByStatus(HelpdeskTicket.Status status);
    List<HelpdeskTicket> findByAssignedEmployeeIsNotNull();
}
