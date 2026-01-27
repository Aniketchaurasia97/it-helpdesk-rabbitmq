package com.helpdesk.ticketservice.service;

import com.helpdesk.ticketservice.config.RabbitMQConfig;
import com.helpdesk.ticketservice.event.TicketCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishTicketCreatedEvent(TicketCreatedEvent event) {
        System.out.println("Publishing event: " + event);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            event
        );
        
        System.out.println("Event published successfully!");
    }
}