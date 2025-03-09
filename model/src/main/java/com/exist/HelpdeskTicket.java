package com.exist;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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

    @Column(nullable = false)
    private String title;         // Title of the ticket

    @Column(nullable = false)
    private String body;          // Body of the ticket

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;        // Enum for status (draft, filed, in progress, closed, duplicate)

    @ManyToOne
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;  // Assignee

    @Column(nullable = false)
    private LocalDateTime createdDate;  // System-generated date for creation

    @Column(nullable = false)
    private String createdBy;          // System-generated created by user

    @Column(nullable = false)
    private LocalDateTime updatedDate;  // System-generated date for last update
    @Column(nullable = false)
    private String updatedBy;          // User who last updated the ticket

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true) //orphan removal ensures that when ticket is removed, it's also deleted from database
    private List<Remark> remarks = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        // Automatically set ticketNumber, createdDate, createdBy during the creation of the ticket
        this.ticketNumber = "TICKET-" + System.currentTimeMillis(); // Generate ticket number based on the current time
        this.createdDate = LocalDateTime.now();
        this.createdBy = "System";  // Set it as "System" for now while there are still no authentications for employee

        // Set updatedDate and updatedBy to be the same as createdDate and createdBy initially
        this.updatedDate = this.createdDate;
        this.updatedBy = this.createdBy;
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
