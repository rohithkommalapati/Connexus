package com.connexus.ticketing_service.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
public class QrGenerator {

    private static final String DEFAULT_FORMAT = "PNG";
    private static final int DEFAULT_SIZE = 300;
    private static final String DEFAULT_FOLDER = "storage/qr";

    public static Path generateQr(String payload, Path outputDir, Integer size, String fileName) {
        if (payload == null || payload.isEmpty()) {
            throw new IllegalArgumentException("QR payload cannot be null or empty");
        }

        try {
            int qrSize = (size != null && size > 0) ? size : DEFAULT_SIZE;
            Path baseDir = (outputDir != null)
                    ? outputDir
                    : Paths.get(System.getProperty("user.dir"), DEFAULT_FOLDER);

            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
            }

            String safeFileName = (fileName != null && !fileName.isBlank())
                    ? fileName
                    : "qr_" + System.currentTimeMillis();

            Path qrPath = baseDir.resolve(safeFileName + "." + DEFAULT_FORMAT.toLowerCase());

            // Encode the payload to QR
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(payload, BarcodeFormat.QR_CODE, qrSize, qrSize);

            MatrixToImageWriter.writeToPath(matrix, DEFAULT_FORMAT, qrPath);

            log.info("QR code generated successfully at: {}", qrPath.toAbsolutePath());
            return qrPath;

        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code", e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    public static Path generateQr(String payload) {
        return generateQr(payload, null, DEFAULT_SIZE, null);
    }
}
