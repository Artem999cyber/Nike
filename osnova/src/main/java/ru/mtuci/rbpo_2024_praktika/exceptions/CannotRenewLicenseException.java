package ru.mtuci.rbpo_2024_praktika.exceptions;

public class CannotRenewLicenseException extends RuntimeException {
    public CannotRenewLicenseException(String message) {
        super(message);
    }
}
