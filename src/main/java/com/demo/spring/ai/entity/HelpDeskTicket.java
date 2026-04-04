package com.demo.spring.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Builder
@Table(name = "helpdesk_tickets")
@AllArgsConstructor
@NoArgsConstructor
public class HelpDeskTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String issueContent;

    private TicketStatus status; // e.g., OPEN, IN_PROGRESS, CLOSED

    private ZonedDateTime createdAt;

    private ZonedDateTime eta;
}
