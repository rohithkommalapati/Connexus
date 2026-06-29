# Connexus
Event Management & Networking Platform
A cloud-native event management and attendee networking platform built with Next.js and Spring Boot microservices. The platform enables users to discover events, register for tickets, download QR-based passes, manage professional connections, and receive event-related notifications.
Overview
This project is designed as a full-stack, microservice-based platform for organizing events and improving attendee engagement. It combines event discovery, registration, QR ticket generation, attendee profile management, connection requests, and notifications into a single scalable system.
Key Features
•	User authentication and profile management: JWT-based signup/login, editable user profiles, skills, bio, and contact information.
•	Event discovery: Browse, search, filter, and view detailed event information including title, description, date, category, and location.
•	Organizer dashboard: Create, update, and manage events from a dedicated organizer interface.
•	Ticket registration: Register for events and generate unique tickets with attendee and event snapshot data.
•	QR-based ticket validation: Generate QR codes for tickets and support authenticity verification using signed payloads.
•	Downloadable ticket passes: Generate downloadable PNG tickets containing event details, attendee information, and QR code.
•	Attendance tracking: Support ticket verification and one-time check-in workflows.
•	Networking: Send, accept, decline, and manage attendee connection requests.
•	Profile QR codes: Generate QR codes for user profiles to simplify contact exchange.
•	Notifications: Store and fetch event updates, registration confirmations, reminders, and connection notifications.
Tech Stack
Layer	Technology
Frontend	Next.js
Backend	Spring Boot microservices
Authentication	JWT-based authentication and authorization
Database	PostgreSQL or MySQL
Storage	AWS S3 or GCP Cloud Storage for QR codes and ticket images
Deployment	Vercel for frontend, AWS ECS or GCP Cloud Run for backend services
Notifications	Database-backed notifications; Google Pub/Sub, Firebase FCM, SES, or SendGrid as future-ready integrations
Microservices
•	User Service: Handles authentication, authorization, user profiles, connection requests, and profile QR code generation.
•	Event Service: Manages event CRUD operations, event discovery, search/filter logic, and attendee registration coordination.
•	Ticket Service: Generates tickets, QR codes, signed ticket payloads, downloadable PNG passes, ticket verification, and check-in state.
•	Notification Service: Stores user notifications and exposes APIs for event reminders, registration updates, and connection-related alerts.
Ticket Service Design
The Ticket Service uses a hybrid generation approach to keep registration fast and ticket downloads flexible. During registration, the service creates a ticket entity, generates a QR code, uploads the QR image to cloud storage, and immediately returns the ticket details. When the user downloads the ticket, the service dynamically renders the full ticket PNG using the stored ticket snapshot, event details, attendee details, and QR image.
Ticket Entity Fields
•	id
•	ticketUid
•	eventId
•	userId
•	attendeeName
•	eventTitle
•	category
•	location
•	eventDate
•	eventStartTime
•	eventEndTime
•	status
•	issuedAt
•	expiresAt
•	qrCodeUrl
•	ticketPngUrl
•	metadataJson
•	signature
•	lastVerifiedAt
•	checkInTime
API Overview
•	Authentication: signup, login, token validation, and protected route access.
•	Users: create, read, update, and manage user profiles.
•	Connections: send, accept, reject, and list connection requests.
•	Events: create, update, delete, search, filter, and view event details.
•	Registration: register a user for an event using userId and eventId, with service-side validation of user and event data.
•	Tickets: create ticket, generate QR code, download ticket PNG, verify authenticity, and perform one-time check-in.
•	Notifications: fetch user-specific notifications and store event or connection updates.
Security Considerations
•	Use JWT for secure authentication and authorization across protected APIs.
•	Store only safe ticket identifiers in QR payloads instead of exposing full sensitive ticket details.
•	Use HMAC signatures to validate ticket authenticity and prevent QR tampering.
•	Allow multiple verification checks, but enforce one-time check-in per ticket.
•	Keep cloud storage objects private where possible and expose downloads through controlled backend APIs.
•	Do not commit secrets, database credentials, API keys, or cloud access tokens to the repository.
Getting Started
Prerequisites: Node.js, Java, Maven or Gradle, Docker, PostgreSQL or MySQL, and a cloud storage account such as AWS S3 or GCP Cloud Storage.
1.	Clone the repository.
2.	Configure environment variables for frontend, backend services, database, JWT secret, and storage provider.
3.	Install frontend dependencies.
4.	Run backend microservices locally or through Docker.
5.	Start the Next.js frontend application.
6.	Register a user, create an event, register for the event, and download a generated ticket PNG.
Environment Variables
•	DATABASE_URL
•	JWT_SECRET
•	STORAGE_BUCKET_NAME
•	STORAGE_ACCESS_KEY
•	STORAGE_SECRET_KEY
•	FRONTEND_BASE_URL
•	USER_SERVICE_URL
•	EVENT_SERVICE_URL
•	TICKET_SERVICE_URL
•	NOTIFICATION_SERVICE_URL
Roadmap
•	User and organizer role categorization.
•	Live chat between attendees or event participants.
•	QR scanning flow for automatic contact exchange.
•	Event-driven notification pipeline using Google Pub/Sub or Kafka.
•	Email and push notification integrations.
•	Payment and cancellation workflows.
•	Admin analytics dashboard for registrations, attendance, and engagement.
Contributing
Contributions are welcome. Please create a feature branch, keep changes focused, follow consistent coding standards, and open a pull request with a clear description of the changes made.
License
This project can be licensed under the MIT License. Update this section based on the license you choose for the repository.


