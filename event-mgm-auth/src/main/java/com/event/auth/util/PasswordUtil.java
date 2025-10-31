package com.event.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
