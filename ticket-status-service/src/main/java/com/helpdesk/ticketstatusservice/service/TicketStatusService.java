package com.helpdesk.ticketstatusservice.service;

import com.helpdesk.ticketstatusservice.dto.StatusHistoryResponse;
import com.helpdesk.ticketstatusservice.dto.StatusSummaryResponse;
import com.helpdesk.ticketstatusservice.dto.StatusUpdateRequest;
import com.helpdesk.ticketstatusservice.exception.InvalidInputException;
import com.helpdesk.ticketstatusservice.exception.ResourceNotFoundException;
import com.helpdesk.ticketstatusservice.model.TicketStatus;
import com.helpdesk.ticketstatusservice.model.TicketStatusHistory;
import com.helpdesk.ticketstatusservice.repository.TicketStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketStatusService {
    
    private final TicketStatusHistoryRepository statusHistoryRepository;
    
    public StatusHistoryResponse updateTicketStatus(StatusUpdateRequest request) {
        // Validate input
        if (request.getTicketId() == null || request.getTicketId() <= 0) {
            throw new InvalidInputException("Ticket ID must be a positive number");
        }
        if (request.getUpdatedBy() == null || request.getUpdatedBy().trim().isEmpty()) {
            throw new InvalidInputException("Updated by field cannot be empty");
        }
        if (request.getStatus() == null) {
            throw new InvalidInputException("Status cannot be null");
        }
        
        TicketStatusHistory statusHistory = new TicketStatusHistory();
        statusHistory.setTicketId(request.getTicketId());
        statusHistory.setStatus(request.getStatus());
        statusHistory.setUpdatedBy(request.getUpdatedBy());
        
        TicketStatusHistory savedHistory = statusHistoryRepository.save(statusHistory);
        return mapToResponse(savedHistory);
    }
    
    public List<StatusHistoryResponse> getStatusHistory(Long ticketId) {
        if (ticketId == null || ticketId <= 0) {
            throw new InvalidInputException("Ticket ID must be a positive number");
        }
        
        List<TicketStatusHistory> history = statusHistoryRepository.findByTicketIdOrderByUpdatedAtDesc(ticketId);
        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No status history found for ticket ID " + ticketId);
        }
        
        return history.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public StatusSummaryResponse getStatusSummary(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        List<TicketStatusHistory> statusUpdates = statusHistoryRepository.findByUpdatedAtBetween(startOfDay, endOfDay);
        
        // Count status updates by status type
        Map<String, Long> statusCounts = new HashMap<>();
        for (TicketStatus status : TicketStatus.values()) {
            statusCounts.put(status.name(), 0L);
        }
        
        for (TicketStatusHistory history : statusUpdates) {
            String statusName = history.getStatus().name();
            statusCounts.put(statusName, statusCounts.get(statusName) + 1);
        }
        
        long totalTickets = statusUpdates.size();
        return new StatusSummaryResponse(date, statusCounts, totalTickets);
    }
    
    public List<StatusHistoryResponse> getAllTickets() {
        List<TicketStatusHistory> latestStatusForAllTickets = statusHistoryRepository.findLatestStatusForAllTickets();
        return latestStatusForAllTickets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private StatusHistoryResponse mapToResponse(TicketStatusHistory history) {
        StatusHistoryResponse response = new StatusHistoryResponse();
        response.setId(history.getId());
        response.setTicketId(history.getTicketId());
        response.setStatus(history.getStatus());
        response.setUpdatedBy(history.getUpdatedBy());
        response.setUpdatedAt(history.getUpdatedAt());
        return response;
    }
}