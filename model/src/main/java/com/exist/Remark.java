package com.exist;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "remarks")
public class Remark extends CreatedDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String message;  // The actual remark content

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private HelpdeskTicket ticket;  // The associated helpdesk ticket


}
