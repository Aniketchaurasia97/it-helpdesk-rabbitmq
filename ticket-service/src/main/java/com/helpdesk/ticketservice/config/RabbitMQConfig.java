package com.helpdesk.ticketservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String EXCHANGE_NAME = "ticket.exchange";
    public static final String QUEUE_NAME = "ticket.created.queue";
    public static final String ROUTING_KEY = "ticket.created";
    
    @Bean
    public DirectExchange ticketExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }
    
    @Bean
    public Queue ticketCreatedQueue() {
        return new Queue(QUEUE_NAME, true, false, false);
    }
    
    @Bean
    public Binding ticketBinding() {
        return BindingBuilder
                .bind(ticketCreatedQueue())
                .to(ticketExchange())
                .with(ROUTING_KEY);
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}