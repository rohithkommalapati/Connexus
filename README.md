# Connexus
# Event Management & Networking Platform

A cloud-native event management and attendee networking platform built with **Next.js** and **Spring Boot microservices**. The platform enables users to discover events, register for tickets, download QR-based passes, manage professional connections, and receive event-related notifications.

---

## Overview

This project is designed as a full-stack, microservice-based platform for organizing events and improving attendee engagement.

It combines:

- Event discovery
- Event registration
- QR-based ticket generation
- Downloadable ticket passes
- Attendee profile management
- Connection requests
- Profile QR codes
- Notification management

The system is designed to be scalable, modular, and suitable for cloud deployment.

---

## Key Features

### User Management

- JWT-based signup and login
- User profile creation and updates
- Profile fields such as name, bio, skills, and contact information
- My Connections section
- Sent, received, pending, accepted, and declined connection requests

### Event Discovery & Management

- Event catalog with list view
- Search and filter events
- Event details page
- Event organizer dashboard
- Create, update, and manage events
- Attendee registration management

### Ticketing & QR Passes

- Register for an event and generate a unique ticket
- Generate unique ticket IDs
- Generate QR codes for tickets
- Store QR codes in cloud storage
- Download ticket as PNG
- View tickets in a My Tickets page
- Track ticket status
- Support ticket verification
- Support one-time check-in

### Networking Features

- Connect button on attendee profiles
- Send connection requests
- Accept or reject received requests
- View pending requests
- Store user connections
- Generate QR code for user profile
- Download profile QR code as PNG
- Future support for QR scanning and automatic contact exchange

### Notifications

- Notification panel for users
- Event reminders
- Registration confirmations
- New connection notifications
- Event update notifications
- Store notifications in database
- Fetch notifications per user
- Future support for email and push notifications

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Next.js |
| Backend | Spring Boot Microservices |
| Authentication | JWT |
| Database | PostgreSQL / MySQL |
| Cloud Storage | AWS S3 / GCP Cloud Storage |
| Deployment | Vercel, AWS ECS, GCP Cloud Run |
| Notifications | Database-backed notifications, Google Pub/Sub, Firebase FCM, SES, SendGrid |

---

## Microservices Architecture

The platform is designed using a microservice-based architecture.

### User Service

Responsible for:

- User registration
- User login
- JWT authentication
- Authorization
- User profile CRUD operations
- Connection requests
- Profile QR code generation

### Event Service

Responsible for:

- Event creation
- Event updates
- Event deletion
- Event listing
- Event search and filtering
- Event details
- Attendee registration coordination

### Ticket Service

Responsible for:

- Ticket creation
- Unique ticket ID generation
- QR code generation
- Ticket PNG generation
- Ticket download
- Ticket authenticity verification
- One-time check-in tracking

### Notification Service

Responsible for:

- Creating notifications
- Storing notifications
- Fetching user notifications
- Event update notifications
- Registration notifications
- Connection request notifications

---

## Ticket Service Design

The Ticket Service follows a simple and maintainable hybrid ticket generation approach.

### Hybrid Ticket Generation Flow

#### Stage 1: Registration

When a user registers for an event:

1. The system creates a ticket entity.
2. A unique ticket ID is generated.
3. A QR code is generated for the ticket.
4. The QR code is uploaded to cloud storage.
5. The QR code URL is stored in the ticket record.
6. The ticket details are returned immediately to the client.

This keeps registration fast and lightweight.

#### Stage 2: Ticket Download

When the user downloads the ticket:

1. The system fetches the ticket details.
2. The system fetches the event and attendee snapshot data.
3. The stored QR code image is retrieved.
4. The full ticket PNG is dynamically rendered.
5. The generated PNG ticket is returned to the user for download.

This avoids unnecessary pre-generation of full ticket images during registration.

---

## Ticket Entity Fields

The Ticket entity contains the following fields:

```text
id
ticketUid
eventId
userId
attendeeName
eventTitle
category
location
eventDate
eventStartTime
eventEndTime
status
issuedAt
expiresAt
qrCodeUrl
ticketPngUrl
metadataJson
signature
lastVerifiedAt
checkInTime

Key Design Decisions
Tickets will be generated and provided as PNG images instead of PDFs.
Operations are synchronous live calls for simplicity.
Event-driven systems such as Pub/Sub or Kafka can be added later.
Payments are not currently integrated.
Ticket cancellation logic is minimal for the initial version.
QR codes should avoid exposing sensitive user or event data.
HMAC signatures can be used for ticket authenticity verification.
Multiple verification checks are allowed.
Check-in should happen only once per ticket.


API Overview
Authentication APIs

User signup
User login
JWT token validation
Protected route access

User APIs

Create user profile
Get user profile
Update user profile
Generate user profile QR code
Download profile QR code

Connection APIs

Send connection request
Accept connection request
Reject connection request
View sent requests
View received requests
View accepted connections

Event APIs

Create event
Update event
Delete event
Get all events
Search events
Filter events
Get event details
Register user for event

Ticket APIs

Create ticket
Generate ticket QR code
Download ticket PNG
Verify ticket authenticity
Check in ticket
Get user tickets

Notification APIs

Create notification
Fetch user notifications
Mark notification as read
Store event update notifications
Store connection request notifications


Recommended API Design
Registration Payload
For event registration, the recommended payload is:
JSON{  "userId": "USER_ID",  "eventId": "EVENT_ID"}Show more lines
The backend should fetch the required user and event details internally using service-to-service communication.
This keeps the client payload small and avoids trusting client-provided user or event data.

QR Code Payload
For better security, the QR code should contain only a minimal payload.
Recommended structure:
JSON{  "ticketUid": "TICKET_UID",  "signature": "HMAC_SIGNATURE"}Show more lines
Avoid embedding full user details, event details, or sensitive information directly inside the QR code.
The backend should verify the ticket by reading the ticket ID and validating the signature.

Verification and Check-In Design
Recommended approach:
Use two separate endpoints:
Plain TextPOST /tickets/{ticketUid}/verifyPOST /tickets/{ticketUid}/check-inShow more lines
This keeps the responsibilities clear.

Verify endpoint checks whether the ticket is valid.
Check-in endpoint validates the ticket and marks it as checked in.
Verification can happen multiple times.
Check-in should happen only once.


Security Considerations

Use JWT for secure authentication and authorization.
Do not expose sensitive data in QR codes.
Use HMAC signatures for ticket authenticity.
Store only safe identifiers in QR payloads.
Validate ticket status before check-in.
Prevent duplicate check-ins.
Keep cloud storage files private where possible.
Serve downloads through controlled backend APIs.
Do not commit secrets or credentials to the repository.
Store environment variables securely.


Cloud Deployment
Frontend
The Next.js frontend can be deployed on:

Vercel

Backend
Spring Boot microservices can be:

Dockerized
Deployed on AWS ECS
Deployed on GCP Cloud Run

Database
Supported database options:

PostgreSQL
MySQL
AWS RDS
GCP Cloud SQL

File Storage
Used for storing:

QR code images
Ticket PNG files
Profile QR codes

Supported storage options:

AWS S3
GCP Cloud Storage

Notifications
Possible notification integrations:

Google Pub/Sub
Firebase Cloud Messaging
Amazon SES
SendGrid


Getting Started
Prerequisites
Make sure you have the following installed:

Node.js
Java
Maven or Gradle
Docker
PostgreSQL or MySQL
AWS or GCP account for storage configuration


Local Setup
1. Clone the Repository
Shellgit clone <repository-url>cd <repository-name>Show more lines
2. Configure Environment Variables
Create the required environment files for frontend and backend services.
Example variables:
Plain Textenv isn’t fully supported. Syntax highlighting is based on Plain Text.DATABASE_URL=JWT_SECRET=STORAGE_BUCKET_NAME=STORAGE_ACCESS_KEY=STORAGE_SECRET_KEY=FRONTEND_BASE_URL=USER_SERVICE_URL=EVENT_SERVICE_URL=TICKET_SERVICE_URL=NOTIFICATION_SERVICE_URL=Show more lines
3. Install Frontend Dependencies
Shellcd frontendnpm installShow more lines
4. Start the Frontend
Shellnpm run devShow more lines
5. Start Backend Services
Run each Spring Boot microservice individually or through Docker.
Shellcd user-servicemvn spring-boot:runShow more lines
Repeat the same for:

Event Service
Ticket Service
Notification Service


Roadmap
Planned future enhancements:

User and organizer role categorization
Live chat between attendees
QR scanning for automatic contact exchange
Event-driven notification pipeline using Pub/Sub or Kafka
Email notifications
Push notifications
Payment integration
Ticket cancellation workflow
Admin analytics dashboard
Attendance reports
Event engagement metrics


Resume Pitch
Designed and developed a cloud-native Event Management & Networking Platform using Next.js and Spring Boot microservices.
Implemented event discovery, QR-based ticketing with downloadable PNG passes, attendee networking through connection requests and profile QR codes, notification management, and cloud-ready deployment architecture using Docker, AWS/GCP storage, and scalable backend services.

License
This project can be licensed under the MIT License.
Update this section based on the license you choose for the repository.
