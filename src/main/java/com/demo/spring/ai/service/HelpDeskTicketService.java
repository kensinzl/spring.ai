package com.demo.spring.ai.service;

import com.demo.spring.ai.entity.HelpDeskTicket;
import com.demo.spring.ai.entity.TicketStatus;
import com.demo.spring.ai.po.TicketRequest;
import com.demo.spring.ai.repository.HelpDeskTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class HelpDeskTicketService {

    private final HelpDeskTicketRepository helpDeskTicketRepository;

    @Autowired
    public HelpDeskTicketService(HelpDeskTicketRepository helpDeskTicketRepository) {
        this.helpDeskTicketRepository = helpDeskTicketRepository;
    }

    public HelpDeskTicket createTicket(TicketRequest ticketInput, String username) {
        HelpDeskTicket ticket = HelpDeskTicket.builder()
                .issueContent(ticketInput.issueContent())
                .username(username)
                .status(TicketStatus.OPEN)
                .createdAt(ZonedDateTime.now())
                .eta(ZonedDateTime.now().plusDays(7))
                .build();
        return helpDeskTicketRepository.save(ticket);
    }

    public List<HelpDeskTicket> getTicketsByUsername(String username) {
        return helpDeskTicketRepository.findByUsername(username);
    }
}
