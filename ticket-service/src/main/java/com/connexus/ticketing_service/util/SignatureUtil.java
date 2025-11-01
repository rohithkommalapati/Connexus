package com.connexus.ticketing_service.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SignatureUtil {

    public static String sign(String data, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] raw = sha256_HMAC.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(raw);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign payload", e);
        }
    }

    public static boolean verify(String data, String signature, String secret) {
        String expected = sign(data, secret);
        return expected.equals(signature);
    }
}
