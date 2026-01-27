# Running the Event-Driven Microservices

## Prerequisites
- Docker installed on your system
- Java 17+ installed
- Maven installed (or use your IDE)

## Step 1: Start RabbitMQ

### Option A: Using Docker Compose (Recommended)
```bash
docker-compose up -d
```

### Option B: Using Docker directly
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

## Step 2: Verify RabbitMQ is Running
- Open RabbitMQ Management UI: http://localhost:15672
- Login with username: `guest`, password: `guest`
- You should see the RabbitMQ dashboard

## Step 3: Start the Services

### Terminal 1 - Start Ticket Service
```bash
cd ticket-service
mvn spring-boot:run
```

### Terminal 2 - Start Ticket Status Service
```bash
cd ticket-status-service
mvn spring-boot:run
```

## Step 4: Test the Event-Driven Flow

1. **Create a ticket** using Swagger UI at http://localhost:8080/swagger-ui.html
2. **Check the logs** - you should see:
   - Ticket Service: "Publishing event: ..." and "Event published successfully!"
   - Status Service: "Received event: ..." and "Created initial OPEN status for ticket: ..."
3. **Verify in RabbitMQ UI** at http://localhost:15672:
   - Go to "Queues" tab
   - You should see `ticket.created.queue`
   - Messages should be processed (Ready = 0, Total = number of tickets created)

## Step 5: Test the Complete Flow

1. Create a ticket via REST API:
```bash
curl -X POST http://localhost:8080/tickets/create \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "employeeName": "John Doe",
    "issueCategory": "LAPTOP",
    "description": "My laptop is not working",
    "priority": "HIGH"
  }'
```

2. Check if initial status was created automatically:
```bash
curl -X GET http://localhost:8081/status/1
```

You should see an OPEN status created by "SYSTEM"!

## Troubleshooting

### RabbitMQ Connection Issues
- Make sure RabbitMQ is running: `docker ps`
- Check if port 5672 is available: `netstat -an | findstr 5672`
- Restart RabbitMQ: `docker restart rabbitmq`

### Service Startup Issues
- Check if ports 8080 and 8081 are free
- Look at the console logs for any errors
- Make sure RabbitMQ is running before starting the services

## What's Happening Behind the Scenes

1. **Ticket Created** → Ticket Service saves to database
2. **Event Published** → Ticket Service sends TicketCreatedEvent to RabbitMQ
3. **Event Consumed** → Status Service receives the event
4. **Auto Status** → Status Service creates initial OPEN status

This is a basic event-driven architecture pattern!