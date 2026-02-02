package com.helpdesk.ticketstatusservice.repository;

import com.helpdesk.ticketstatusservice.model.TicketStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketStatusHistoryRepository extends JpaRepository<TicketStatusHistory, Long> {
    
    List<TicketStatusHistory> findByTicketIdOrderByUpdatedAtDesc(Long ticketId);
    
    List<TicketStatusHistory> findByUpdatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    // Custom query to get the latest status for each ticket
    @Query("SELECT h FROM TicketStatusHistory h WHERE h.updatedAt = " +
           "(SELECT MAX(h2.updatedAt) FROM TicketStatusHistory h2 WHERE h2.ticketId = h.ticketId)")
    List<TicketStatusHistory> findLatestStatusForAllTickets();
}