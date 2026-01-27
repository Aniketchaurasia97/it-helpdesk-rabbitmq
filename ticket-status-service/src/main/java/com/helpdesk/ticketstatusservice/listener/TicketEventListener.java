package com.helpdesk.ticketstatusservice.listener;

import com.helpdesk.ticketstatusservice.config.RabbitMQConfig;
import com.helpdesk.ticketstatusservice.event.TicketCreatedEvent;
import com.helpdesk.ticketstatusservice.model.TicketStatus;
import com.helpdesk.ticketstatusservice.model.TicketStatusHistory;
import com.helpdesk.ticketstatusservice.repository.TicketStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketEventListener {
    
    private final TicketStatusHistoryRepository statusHistoryRepository;
    
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleTicketCreatedEvent(TicketCreatedEvent event) {
        System.out.println("Received event: " + event);
        
        try {
            // Automatically create initial status as OPEN
            TicketStatusHistory initialStatus = new TicketStatusHistory();
            initialStatus.setTicketId(event.getTicketId());
            initialStatus.setStatus(TicketStatus.OPEN);
            initialStatus.setUpdatedBy("SYSTEM");
            
            statusHistoryRepository.save(initialStatus);
            
            System.out.println("Created initial OPEN status for ticket: " + event.getTicketId());
            
        } catch (Exception e) {
            System.err.println("Error processing ticket created event: " + e.getMessage());
        }
    }
}