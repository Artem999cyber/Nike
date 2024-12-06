package ru.mtuci.rbpo_2024_praktika.exceptions;

public class LicenseNotFoundException extends RuntimeException {
    public LicenseNotFoundException(String message) {
        super(message);
    }
}
