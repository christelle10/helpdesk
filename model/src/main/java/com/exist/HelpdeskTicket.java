package com.exist;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tickets")
public class HelpdeskTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ticketNumber;  // Auto-generated ticket number

    private String title;         // Title of the ticket
    private String body;          // Body of the ticket

    @Enumerated(EnumType.STRING)
    private Status status;        // Enum for status (draft, filed, inprogress, closed, duplicate)

    @ManyToOne
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;  // Assignee

    private LocalDateTime createdDate;  // System-generated date for creation
    private String createdBy;          // System-generated created by user

    private LocalDateTime updatedDate;  // System-generated date for last update
    private String updatedBy;          // User who last updated the ticket

    @Column(nullable = true)
    private String remarks;

    @PrePersist
    public void onCreate() {
        // Automatically set ticketNumber, createdDate, and createdBy during the creation of the ticket
        this.ticketNumber = "TICKET-" + System.currentTimeMillis(); // Generate ticket number based on the current time
        this.createdDate = LocalDateTime.now();
        this.createdBy = "System";  // Set it as "System" or use actual authenticated user
    }

    @PreUpdate
    public void onUpdate() {
        // Update the last updated date and user during updates
        this.updatedDate = LocalDateTime.now();
        this.updatedBy = "System";  // Set it as "System" or use actual authenticated user
    }

    // Enum for ticket status
    public enum Status {
        DRAFT,
        FILED,
        IN_PROGRESS,
        CLOSED,
        DUPLICATE
    }
}
