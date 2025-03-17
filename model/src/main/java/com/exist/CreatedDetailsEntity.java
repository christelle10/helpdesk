package com.exist;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class CreatedDetailsEntity {

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDateTime.now();

        // Get authenticated user's name (fallback to "System")
        String username = JwtAuthenticationUtil.getAuthenticatedEmployeeName();
        this.createdBy = (username != null) ? username : "System";

        // Handle `HelpdeskTicket`-specific logic here
        if (this instanceof HelpdeskTicket ticket) {
            ticket.setTicketNumber("TICKET-" + System.currentTimeMillis());
            ticket.setUpdatedDate(this.createdDate);
            ticket.setUpdatedBy(this.createdBy);
        }

        // Handle `Remark`-specific logic
        if (this instanceof Remark remark) {
            // Ensure remark is linked properly
            if (remark.getTicket() != null) {
                remark.setCreatedBy(this.createdBy); // Same user as the one creating the remark
            }
        }
    }
}
