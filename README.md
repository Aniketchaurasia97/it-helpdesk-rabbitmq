# IT Helpdesk Ticket System - Event-Driven Microservices

A beginner-friendly **event-driven microservices** project for managing IT helpdesk tickets, built with Spring Boot and RabbitMQ. Perfect for learning microservices and messaging patterns!

## What You'll Learn

- How to build event-driven microservices
- RabbitMQ message broker integration
- Asynchronous communication between services
- Publisher-Subscriber pattern
- Basic REST API development
- Database operations with JPA and H2
- Clean code structure with layers (Controller → Service → Repository → Model)

## Architecture

This project demonstrates **event-driven architecture** with:

1. **ticket-service** (Port 8080) - Creates tickets and publishes events
2. **ticket-status-service** (Port 8081) - Consumes events and manages status
3. **RabbitMQ** - Message broker for asynchronous communication

### Event Flow
```
Ticket Created → Event Published → RabbitMQ → Event Consumed → Auto Status Created
```

## What You Need

- Java 17 or higher
- Maven 3.6+
- Docker (for RabbitMQ)
- Any IDE (IntelliJ IDEA, Eclipse, VS Code)

## Quick Start

### 1. Start RabbitMQ
```bash
docker-compose up -d
```

### 2. Start the Services
```bash
# Terminal 1
cd ticket-service
mvn spring-boot:run

# Terminal 2  
cd ticket-status-service
mvn spring-boot:run
```

### 3. Test the Event Flow
1. Create a ticket at http://localhost:8080/swagger-ui.html
2. Check RabbitMQ UI at http://localhost:15672 (guest/guest)
3. Verify auto-created status at http://localhost:8081/swagger-ui.html

## Key Features

**Event-Driven Communication:**
- ✅ Automatic event publishing when tickets are created
- ✅ Asynchronous message processing
- ✅ Loose coupling between services
- ✅ RabbitMQ integration with Spring AMQP

**Original Features:**
- ✅ Clean layered architecture
- ✅ H2 in-memory databases
- ✅ Swagger documentation
- ✅ Simple, beginner-friendly code

## Learning Resources

- Check `run-with-rabbitmq.md` for detailed setup instructions
- Look at `api-samples.md` for API examples
- Explore `PROJECT-STRUCTURE.md` for code organization
- Monitor RabbitMQ at http://localhost:15672

Perfect for understanding how modern microservices communicate asynchronously!