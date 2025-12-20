package com.careHive.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class BookingCodeGenerator {

    private static final String PREFIX = "CH";
    private static final String ALPHA_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private BookingCodeGenerator() {}

    public static String generate(LocalDateTime startTime, double durationHours) {
        String durationPart = String.format("%02d", normalizeDuration(durationHours));
        String randomPart = randomAlphaNum(4);
        String datePart = startTime.format(DATE_FORMAT);

        return String.format(
                "%s-%s-%s-%s",
                PREFIX,
                durationPart,
                randomPart,
                datePart
        );
    }

    private static int normalizeDuration(double durationHours) {
        return (int) Math.round(durationHours);
    }

    private static String randomAlphaNum(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHA_NUM.charAt(RANDOM.nextInt(ALPHA_NUM.length())));
        }
        return sb.toString();
    }
}
