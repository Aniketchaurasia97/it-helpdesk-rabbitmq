package com.helpdesk.ticketservice.service;

import com.helpdesk.ticketservice.dto.TicketCreateRequest;
import com.helpdesk.ticketservice.dto.TicketResponse;
import com.helpdesk.ticketservice.event.TicketCreatedEvent;
import com.helpdesk.ticketservice.exception.InvalidInputException;
import com.helpdesk.ticketservice.exception.ResourceNotFoundException;
import com.helpdesk.ticketservice.model.Priority;
import com.helpdesk.ticketservice.model.Ticket;
import com.helpdesk.ticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final EventPublisher eventPublisher;
    
    public TicketResponse createTicket(TicketCreateRequest request) {
        // Validate input
        if (request.getEmployeeId() == null || request.getEmployeeId() <= 0) {
            throw new InvalidInputException("Employee ID must be a positive number");
        }
        if (request.getEmployeeName() == null || request.getEmployeeName().trim().isEmpty()) {
            throw new InvalidInputException("Employee name cannot be empty");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new InvalidInputException("Description cannot be empty");
        }
        
        Ticket ticket = new Ticket();
        ticket.setEmployeeId(request.getEmployeeId());
        ticket.setEmployeeName(request.getEmployeeName());
        ticket.setIssueCategory(request.getIssueCategory());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        
        Ticket savedTicket = ticketRepository.save(ticket);
        
        // Publish event after successful save
        TicketCreatedEvent event = new TicketCreatedEvent(
            savedTicket.getTicketId(),
            savedTicket.getEmployeeId(),
            savedTicket.getPriority().name(),
            savedTicket.getCreatedAt()
        );
        
        eventPublisher.publishTicketCreatedEvent(event);
        
        return mapToResponse(savedTicket);
    }
    
    public TicketResponse getTicketById(Long ticketId) {
        if (ticketId == null || ticketId <= 0) {
            throw new InvalidInputException("Ticket ID must be a positive number");
        }
        
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isPresent()) {
            return mapToResponse(ticket.get());
        }
        throw new ResourceNotFoundException("Ticket with ID " + ticketId + " not found");
    }
    
    public List<TicketResponse> getTicketsByEmployeeId(Long employeeId) {
        if (employeeId == null || employeeId <= 0) {
            throw new InvalidInputException("Employee ID must be a positive number");
        }
        
        List<Ticket> tickets = ticketRepository.findByEmployeeId(employeeId);
        return tickets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TicketResponse> getTicketsByPriority(Priority priority) {
        List<Ticket> tickets = ticketRepository.findByPriority(priority);
        return tickets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TicketResponse> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setTicketId(ticket.getTicketId());
        response.setEmployeeId(ticket.getEmployeeId());
        response.setEmployeeName(ticket.getEmployeeName());
        response.setIssueCategory(ticket.getIssueCategory());
        response.setDescription(ticket.getDescription());
        response.setPriority(ticket.getPriority());
        response.setCreatedAt(ticket.getCreatedAt());
        return response;
    }
}