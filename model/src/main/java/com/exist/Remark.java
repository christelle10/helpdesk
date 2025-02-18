package com.exist;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "remarks")
public class Remark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;  // The actual remark content

    private LocalDateTime createdDate;  // Timestamp when the remark was added

    private String createdBy;  // User who added the remark

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private HelpdeskTicket ticket;  // The associated helpdesk ticket

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.createdBy = "System";  // Replace with actual authenticated user if needed
    }
}
