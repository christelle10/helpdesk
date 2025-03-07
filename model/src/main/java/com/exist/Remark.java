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

    @Column
    private String message;  // The actual remark content

    @Column(nullable = false)
    private LocalDateTime createdDate;  // Timestamp when the remark was added

    @Column(nullable = false)
    private String createdBy;  // User who added the remark

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private HelpdeskTicket ticket;  // The associated helpdesk ticket

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.createdBy = "System";  // Set it as "System" for now since no Spring Security applied yet
    }
}
