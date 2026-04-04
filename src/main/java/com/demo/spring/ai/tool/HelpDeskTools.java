package com.demo.spring.ai.tool;


import com.demo.spring.ai.entity.HelpDeskTicket;
import com.demo.spring.ai.po.TicketRequest;
import com.demo.spring.ai.service.HelpDeskTicketService;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class HelpDeskTools {

    private final HelpDeskTicketService helpDeskTicketService;

    @Autowired
    public HelpDeskTools(HelpDeskTicketService helpDeskTicketService) {
        this.helpDeskTicketService = helpDeskTicketService;
    }

    @Tool(name = "createTicket", description = "Create the Issue Ticket")
    String createTicket(@ToolParam(description = "Details to create a Issue ticket") TicketRequest ticketRequest,
                        ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");
        log.info("Creating support ticket for user: {} with details: {}", username, ticketRequest);
        HelpDeskTicket savedTicket = this.helpDeskTicketService.createTicket(ticketRequest, username);
        return "Issue Ticket No." + savedTicket.getId() + " created successfully for user " + savedTicket.getUsername();
    }

    @Tool(description = "Fetch the status of the tickets based on a given username")
    List<HelpDeskTicket> getTicketStatus(ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");
        List<HelpDeskTicket> tickets =  this.helpDeskTicketService.getTicketsByUsername(username);
        log.info("Found {} tickets for user: {}", tickets.size(), username);
        return tickets;
    }
}
