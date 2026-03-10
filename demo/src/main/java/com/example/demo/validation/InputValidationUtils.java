package com.example.demo.validation;

import java.util.regex.Pattern;

public final class InputValidationUtils {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{2,19}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z\\s.'-]{1,49}$");
    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@#$%^&+=!?._-]{8,64}$");

    private InputValidationUtils() {
    }

    public static boolean isValidUsername(String value) {
        return value != null && USERNAME_PATTERN.matcher(value.trim()).matches();
    }

    public static boolean isValidEmail(String value) {
        return value != null && EMAIL_PATTERN.matcher(value.trim()).matches();
    }

    public static boolean isValidPhoneNumber(String value) {
        return value != null && PHONE_PATTERN.matcher(value.trim()).matches();
    }

    public static boolean isValidName(String value) {
        return value != null && NAME_PATTERN.matcher(value.trim()).matches();
    }

    public static boolean isValidPassword(String value) {
        return value != null && STRONG_PASSWORD_PATTERN.matcher(value).matches();
    }
}
