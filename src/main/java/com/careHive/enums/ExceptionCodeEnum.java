package com.careHive.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ExceptionCodeEnum {

    // ==== COMMON / SYSTEM ERRORS ====
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    UNEXPECTED_ERROR("UNEXPECTED_ERROR"),
    DATABASE_ERROR("DATABASE_ERROR"),
    NULL_POINTER("NULL_POINTER"),
    MAPPING_ERROR("MAPPING_ERROR"),
    FORMAT_ERROR("FORMAT_ERROR"),

    DUPLICATE_EMAIL("DUPLICATE_EMAIL"),
    ADMIN_ALREADY_EXISTS("ADMIN_ALREADY_EXISTS"),

    PROFILE_NOT_FOUND("PROFILE_NOT_FOUND"),
    DUPLICATE_PROFILE("DUPLICATE_PROFILE"),

    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND"),
    PAYMENT_ALREADY_EXISTS("PAYMENT_ALREADY_EXISTS"),
    PAYMENT_FAILED("PAYMENT_FAILED"),

    FILE_NOT_FOUND("FILE_NOT_FOUND"),


    DUPLICATE_TEMPLATE("DUPLICATE_TEMPLATE"),
    TEMPLATE_ALREADY_EXISTS("TEMPLATE_ALREADY_EXISTS"),
    TEMPLATE_NOT_FOUND("TEMPLATE_NOT_FOUND"),

    // ==== AUTH & SECURITY ====
    UNAUTHORIZED("UNAUTHORIZED"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
    TOKEN_EXPIRED("TOKEN_EXPIRED"),
    TOKEN_INVALID("TOKEN_INVALID"),
    FORBIDDEN("FORBIDDEN"),

    DUPLICATE_SERVICE("DUPLICATE_SERVICE"),
    SERVICE_NOT_FOUND("SERVICE_NOT_FOUND"),

    BOOKING_NOT_FOUND("BOOKING_NOT_FOUND"),
    DUPLICATE_BOOKING("DUPLICATE_BOOKING"),

    // ==== VALIDATION / INPUT ====
    BAD_REQUEST("BAD_REQUEST"),
    INVALID_ARGUMENT("INVALID_ARGUMENT"),
    VALIDATION_FAILED("VALIDATION_FAILED"),
    PASSWORD_TOO_WEAK("PASSWORD_TOO_WEAK"),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH"),
    PASSWORD_IS_EMPTY("PASSWORD_IS_EMPTY"),
    PASSWORD_RESET_FAILED("PASSWORD_RESET_FAILED");


    private final String value;

    private static final Map<String, ExceptionCodeEnum> valueToEnumMap = new HashMap<>();

    static {
        for (ExceptionCodeEnum code : ExceptionCodeEnum.values()) {
            valueToEnumMap.put(code.getValue().toUpperCase(), code);
        }
    }

    ExceptionCodeEnum(String value) {
        this.value = value;
    }

    public static ExceptionCodeEnum fromValue(String value) {
        ExceptionCodeEnum code = valueToEnumMap.get(value.toUpperCase());
        if (code != null) {
            return code;
        }
        throw new IllegalArgumentException("Invalid exception code: " + value);
    }
}

