@echo off
echo Starting Ticket Status Service on port 8081...
cd ticket-status-service
mvn spring-boot:run
pause