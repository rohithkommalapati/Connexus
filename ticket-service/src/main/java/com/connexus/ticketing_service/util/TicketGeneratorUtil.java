package com.connexus.ticketing_service.util;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import javax.imageio.ImageIO;

public class TicketGeneratorUtil {

    private static final Path OUTPUT_DIR = Paths.get("tickets");

    public static Path generateStyledTicketPng(
            String ticketUid,
            String eventName,
            String attendeeName,
            String eventDate,
            String startTime,
            String location,
            String venue,
            String category,
            Image qrImage) throws Exception {

        if (!Files.exists(OUTPUT_DIR)) Files.createDirectories(OUTPUT_DIR);
        Path filePath = OUTPUT_DIR.resolve(ticketUid + ".png");

        int width = 1200;
        int height = 550;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Background Gradient ---
        GradientPaint bg = new GradientPaint(0, 0,
                new Color(255, 242, 212), width, height, new Color(255, 228, 196));
        g.setPaint(bg);
        g.fillRect(0, 0, width, height);

        // --- Main Ticket Container ---
        int margin = 40;
        int ticketWidth = width - 2 * margin;
        int ticketHeight = height - 2 * margin;
        RoundRectangle2D.Double card = new RoundRectangle2D.Double(margin, margin, ticketWidth, ticketHeight, 40, 40);

        g.setColor(Color.WHITE);
        g.fill(card);
        g.setColor(new Color(220, 180, 100));
        g.setStroke(new BasicStroke(4f));
        g.draw(card);

        // --- Left Section Background ---
        g.setColor(new Color(250, 248, 240));
        g.fillRoundRect(margin + 10, margin + 10, (int) (ticketWidth * 0.65), ticketHeight - 20, 30, 30);

        // --- Text Colors ---
        Color titleColor = new Color(74, 38, 115);
        Color textColor = new Color(60, 60, 60);
        Color accentColor = new Color(200, 160, 60);

        // --- Header Section ---
        g.setFont(new Font("SansSerif", Font.BOLD, 42));
        g.setColor(titleColor);
        g.drawString(eventName, margin + 60, margin + 100);

        g.setFont(new Font("SansSerif", Font.PLAIN, 26));
        g.setColor(accentColor);
        g.drawString("Event Pass", margin + 60, margin + 140);

        // --- Attendee Info ---
        int textY = margin + 200;
        g.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g.setColor(textColor);
        g.drawString("Attendee Name: " + attendeeName, margin + 60, textY);
        textY += 40;
        g.drawString("Ticket ID: " + ticketUid, margin + 60, textY);
        textY += 40;
        g.drawString("Event Date: " + eventDate, margin + 60, textY);
        textY += 40;
        g.drawString("Start Time: " + startTime, margin + 60, textY);
        textY += 40;
        g.drawString("Location: " + location, margin + 60, textY);
        textY += 40;
        g.drawString("Venue: " + venue, margin + 60, textY);
        textY += 40;
        g.drawString("Category: " + category, margin + 60, textY);

        // --- QR Section ---
        int qrBoxX = margin + (int) (ticketWidth * 0.7);
        int qrBoxY = margin + 60;
        int qrBoxWidth = (int) (ticketWidth * 0.25);
        int qrBoxHeight = ticketHeight - 120;

        RoundRectangle2D.Double qrBox = new RoundRectangle2D.Double(qrBoxX, qrBoxY, qrBoxWidth, qrBoxHeight, 30, 30);
        g.setColor(new Color(230, 245, 250));
        g.fill(qrBox);
        g.setColor(new Color(180, 180, 180));
        g.draw(qrBox);

        // --- QR Title ---
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.setColor(titleColor);
        FontMetrics fm = g.getFontMetrics();
        String qrTitle = "Admit One";
        g.drawString(qrTitle, qrBoxX + (qrBoxWidth - fm.stringWidth(qrTitle)) / 2, qrBoxY + 50);

        int qrImgSize = 250;
        int qrImgX = qrBoxX + (qrBoxWidth - qrImgSize) / 2;
        int qrImgY = qrBoxY + 80;
        g.drawImage(qrImage, qrImgX, qrImgY, qrImgSize, qrImgSize, null);

        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.setColor(textColor);
        String instr = "Present this QR code at the event entrance";
        fm = g.getFontMetrics();
        g.drawString(instr, qrBoxX + (qrBoxWidth - fm.stringWidth(instr)) / 2, qrBoxY + qrBoxHeight - 30);

        g.setFont(new Font("SansSerif", Font.ITALIC, 16));
        g.setColor(new Color(150, 100, 80));
        g.drawString("No transfers or exchanges allowed", margin + 60, height - 50);

        g.dispose();

        ImageIO.write(image, "PNG", filePath.toFile());
        return filePath;
    }
}
