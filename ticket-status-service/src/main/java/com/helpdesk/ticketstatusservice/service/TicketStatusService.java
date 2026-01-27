package com.helpdesk.ticketstatusservice.service;

import com.helpdesk.ticketstatusservice.dto.StatusHistoryResponse;
import com.helpdesk.ticketstatusservice.dto.StatusSummaryResponse;
import com.helpdesk.ticketstatusservice.dto.StatusUpdateRequest;
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
        TicketStatusHistory statusHistory = new TicketStatusHistory();
        statusHistory.setTicketId(request.getTicketId());
        statusHistory.setStatus(request.getStatus());
        statusHistory.setUpdatedBy(request.getUpdatedBy());
        
        TicketStatusHistory savedHistory = statusHistoryRepository.save(statusHistory);
        return mapToResponse(savedHistory);
    }
    
    public List<StatusHistoryResponse> getStatusHistory(Long ticketId) {
        List<TicketStatusHistory> history = statusHistoryRepository.findByTicketIdOrderByUpdatedAtDesc(ticketId);
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