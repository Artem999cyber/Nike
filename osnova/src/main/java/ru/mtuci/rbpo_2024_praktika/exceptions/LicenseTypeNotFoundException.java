package ru.mtuci.rbpo_2024_praktika.exceptions;

public class LicenseTypeNotFoundException extends RuntimeException{
    public LicenseTypeNotFoundException(String message) {
        super(message);
    }
}
