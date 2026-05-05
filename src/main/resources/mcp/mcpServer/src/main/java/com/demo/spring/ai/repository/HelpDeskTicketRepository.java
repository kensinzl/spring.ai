package com.demo.spring.ai.repository;

import com.demo.spring.ai.entity.HelpDeskTicket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpDeskTicketRepository extends CrudRepository<HelpDeskTicket,Long> {
    @Query("SELECT t FROM HelpDeskTicket t WHERE t.status = TicketStatus.OPEN and t.username = :username")
    List<HelpDeskTicket> findByUsername(@Param("username") String username);
}
