package com.connexus.ticketing_service.service;

import com.connexus.ticketing_service.dto.*;
import com.connexus.ticketing_service.exception.*;
import com.connexus.ticketing_service.feign.EventFeign;
import com.connexus.ticketing_service.feign.UserFeign;
import com.connexus.ticketing_service.model.Ticket;
import com.connexus.ticketing_service.model.TicketStatus;
import com.connexus.ticketing_service.repository.TicketRepository;
import com.connexus.ticketing_service.util.QrGenerator;
import com.connexus.ticketing_service.util.SignatureUtil;
import com.connexus.ticketing_service.util.TicketGeneratorUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserFeign userFeign;
    private final EventFeign eventFeign;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${ticketing_service.storage.path}")
    private String storagePath;

    @Value("${ticketing_service.hmac.secret}")
    private String hmacSecret;

    @Value("${ticketing_service.qr.size:300}")
    private int qrSize;

    public TicketService(TicketRepository ticketRepository,
                         UserFeign userFeign,
                         EventFeign eventFeign) {
        this.ticketRepository = ticketRepository;
        this.userFeign = userFeign;
        this.eventFeign = eventFeign;
    }

    @Transactional
    public TicketResponseDTO createTicket(TicketRequestDTO req) throws Exception {
        log.info("Creating ticket for Event ID={} and User ID={}", req.getEventId(), req.getUserId());

        EventFeignDTO event = fetchEvent(req.getEventId());
        UserFeignDTO user = fetchUser(req.getUserId());

        String attendeeName = req.getAttendeeName();
        if (attendeeName == null || attendeeName.isBlank()) {
            attendeeName = buildFullName(user.getFirstName(), user.getLastName());
        }

        String ticketUid = generateTicketUid();

        Map<String, Object> payload = buildPayload(ticketUid, attendeeName, event, user);
        String payloadJson = mapper.writeValueAsString(payload);
        String signature = SignatureUtil.sign(payloadJson, hmacSecret);

        Map<String, Object> fullPayload = new LinkedHashMap<>(payload);
        fullPayload.put("signature", signature);
        String qrContent = mapper.writeValueAsString(fullPayload);

        Path qrPath = generateAndStoreQr(ticketUid, qrContent);

        Ticket ticket = buildAndSaveTicket(ticketUid, attendeeName, event, user, payloadJson, signature, qrPath);

        log.info("Ticket created successfully: {}", ticketUid);
        return TicketResponseDTO.builderDto(ticket);
    }

    /**
     * Ticket details.
     */
    public TicketResponseDTO getTicketDetails(String ticketUid) {
        Ticket ticket = ticketRepository.findByTicketUid(ticketUid)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        return TicketResponseDTO.builderDto(ticket);
    }

    public List<TicketResponseDTO> getAllTicketsForUser(Long userId) {
        List<Ticket> tickets = ticketRepository.findByUserId(userId);

        return tickets.stream()
                .map(TicketResponseDTO::builderDto)
                .collect(Collectors.toList());
    }

    public String generateTicketBase64(String ticketUid) throws Exception {
        Path pngPath = generateTicketPng(ticketUid);
        byte[] imageBytes = Files.readAllBytes(pngPath);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public Path generateTicketPng(String ticketUid) throws Exception {
        Ticket ticket = ticketRepository.findByTicketUid(ticketUid)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        Path qrPath;
        if (ticket.getQrCodeUrl() != null && Files.exists(Paths.get(ticket.getQrCodeUrl()))) {
            qrPath = Paths.get(ticket.getQrCodeUrl());
        } else {
            qrPath = QrGenerator.generateQr(
                    ticket.getTicketUid(),
                    null,
                    300,
                    ticket.getTicketUid()
            );
            ticket.setQrCodeUrl(qrPath.toAbsolutePath().toString());
            ticketRepository.save(ticket);
        }

        Image qrImage = ImageIO.read(qrPath.toFile());

        // Step 4: Generate the styled ticket PNG
        return TicketGeneratorUtil.generateStyledTicketPng(
                ticket.getTicketUid(),
                ticket.getEventTitle(),
                ticket.getAttendeeName(),
                ticket.getEventDate() != null ? ticket.getEventDate().toString() : "N/A",
                ticket.getEventStartTime() != null ? ticket.getEventStartTime().toString() : "N/A",
                ticket.getLocation() != null ? ticket.getLocation() : "N/A",
                ticket.getVenue(),
                ticket.getCategory() != null ? ticket.getCategory() : "General",
                qrImage
        );
    }


    /**
     * Fetches event and user details using the Feign clients.
     */
    private EventFeignDTO fetchEvent(Long eventId) {
        try {
            return eventFeign.getEventById(eventId);
        } catch (Exception e) {
            log.error("Failed to fetch eventId={}", eventId, e);
            throw new EventServiceException("Unable to fetch event details for ID: " + eventId);
        }
    }

    private UserFeignDTO fetchUser(Long userId) {
        try {
            return userFeign.getUserById(userId);
        } catch (Exception e) {
            log.error("Failed to fetch userId={}", userId, e);
            throw new UserServiceException("Unable to fetch user details for ID: " + userId);
        }
    }

    /**
     * Verify Ticket's Signature.
     */
    @Transactional
    public TicketVerifyResponse verify(TicketVerifyRequest req) throws Exception, InvalidSignatureException {

        log.info("Verifying ticket request...");
        if (req.getPayload() != null && !req.getPayload().isBlank()) {
            return verifyFromPayload(req);
        } else if (req.getTicketId() != null && !req.getTicketId().isBlank()) {
            return verifyFromTicketId(req);
        } else {
            throw new IllegalArgumentException("Either payload or ticketUid must be provided");
        }
    }

    private TicketVerifyResponse verifyFromPayload(TicketVerifyRequest req) throws Exception {

        Map<String, Object> map = mapper.readValue(req.getPayload(), new TypeReference<Map<String, Object>>() {});
        String signature = (String) map.remove("signature");
        String plainPayload = mapper.writeValueAsString(map);

        if (!SignatureUtil.verify(plainPayload, signature, hmacSecret)) {
            log.warn("Invalid signature detected in QR payload");
            return TicketVerifyResponse.builder()
                    .valid(false)
                    .reason("Invalid signature in payload")
                    .ticketId((String) map.get("ticketUid"))
                    .build();
        }

        String ticketId = (String) map.get("ticketUid");
        Ticket ticket = ticketRepository.findByTicketUid(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        if (req.isCheckIn()) {
            try {
                ticket = updateTicketStatus(ticketId, TicketStatus.CHECKED_IN);
            } catch (IllegalStateException ex) {
                log.warn("Ticket {} cant be checked in: {}", ticketId, ex.getMessage());
                return TicketVerifyResponse.builder()
                        .valid(false)
                        .reason(ex.getMessage())
                        .ticketId(ticketId)
                        .build();
            }
        }
        log.info("Ticket verified successfully from payload: {}", ticketId);
        return TicketVerifyResponse.buildVerificationResponse(ticket, true);
    }

    private TicketVerifyResponse verifyFromTicketId(TicketVerifyRequest req) throws Exception {
        Ticket ticket = ticketRepository.findByTicketUid(req.getTicketId())
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        boolean validSignature = SignatureUtil.verify(ticket.getMetadataJson(), ticket.getSignature(), hmacSecret);
        if (!validSignature) {
            log.warn("Signature mismatch for ticket {}", req.getTicketId());
            return TicketVerifyResponse.builder()
                    .valid(false)
                    .reason("Invalid signature (stored)")
                    .ticketId(req.getTicketId())
                    .build();
        }

        if (req.isCheckIn()) {
            try {
                 ticket = updateTicketStatus(req.getTicketId(), TicketStatus.CHECKED_IN);
            } catch (IllegalStateException ex) {
                log.warn("Ticket {} cannot be checked in: {}", req.getTicketId(), ex.getMessage());
                return TicketVerifyResponse.builder()
                        .valid(false)
                        .reason(ex.getMessage())
                        .ticketId(req.getTicketId())
                        .build();
            }
        }

        log.info("Ticket verified by ID successfully: {}", req.getTicketId());

        return TicketVerifyResponse.buildVerificationResponse(ticket, true);
    }

    @Transactional
    public Ticket updateTicketStatus(String ticketUid, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findByTicketUid(ticketUid)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        if (ticket.getEventEndTime() != null && ticket.getEventDate() != null) {
            LocalDateTime eventEndDateTime = LocalDateTime.of(ticket.getEventDate(), ticket.getEventEndTime());
            if (LocalDateTime.now().isAfter(eventEndDateTime)) {
                ticket.setStatus(TicketStatus.EXPIRED);
                ticket.setLastVerifiedAt(LocalDateTime.now());
                ticketRepository.save(ticket);
                return ticket;
            }
        }

        if (ticket.getStatus() == TicketStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot modify ticket that is already checked in");
        }

        if (newStatus == TicketStatus.CANCELLED || newStatus == TicketStatus.CHECKED_IN) {
            ticket.setStatus(newStatus);
            if (newStatus == TicketStatus.CHECKED_IN) {
                ticket.setCheckInTime(LocalDateTime.now());
            }
            ticket.setLastVerifiedAt(LocalDateTime.now());
            ticketRepository.save(ticket);
        }

        return ticket;
    }

    private String buildFullName(String first, String last) {
        return (first == null ? "" : first) + (last == null ? "" : " " + last);
    }

    private String generateTicketUid() {
        return "TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Map<String, Object> buildPayload(String ticketUid, String attendeeName,
                                             EventFeignDTO event, UserFeignDTO user) {
        LocalDateTime startTime = event.getStartTime();
        LocalDate eventDate = startTime.toLocalDate();
        LocalTime eventTime = startTime.toLocalTime();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("ticketUid", ticketUid);
        payload.put("attendeeName", attendeeName);
        payload.put("eventId", event.getId());
        payload.put("eventTitle", event.getTitle());
        payload.put("category", event.getCategory());
        payload.put("location", event.getLocation());
        payload.put("venue",event.getVenue());
        payload.put("eventDate", eventDate.toString());
        payload.put("eventTime", eventTime.toString());
        payload.put("userId", user.getId());
        payload.put("issuedAt", LocalDateTime.now().toString());
        return payload;
    }

    private Path generateAndStoreQr(String ticketUid, String qrContent) throws Exception {
        String filename = ticketUid + ".png";
        Path out = Path.of(storagePath).resolve(filename);
        QrGenerator.generateQr(qrContent, out, qrSize, filename);
        log.debug("QR code generated at {}", out.toAbsolutePath());
        return out;
    }

    private Ticket buildAndSaveTicket(String ticketUid, String attendeeName, EventFeignDTO event,
                                      UserFeignDTO user, String payloadJson, String signature, Path qrPath) {

        Ticket t = Ticket.builder()
                .ticketUid(ticketUid)
                .eventId(event.getId())
                .userId(user.getId())
                .attendeeName(attendeeName)
                .eventTitle(event.getTitle())
                .category(event.getCategory())
                .location(event.getLocation())
                .venue(event.getVenue())
                .eventDate(event.getStartTime().toLocalDate())
                .eventStartTime(event.getStartTime().toLocalTime())
                .status(TicketStatus.ACTIVE)
                .qrCodeUrl(qrPath.toAbsolutePath().toString())
                .metadataJson(payloadJson)
                .signature(signature)
                .issuedAt(LocalDateTime.now())
                .build();

        Ticket saved = ticketRepository.save(t);
        log.debug("Ticket entity persisted with ID={}", saved.getId());
        return saved;
    }

}

