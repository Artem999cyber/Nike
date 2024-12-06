package ru.mtuci.rbpo_2024_praktika.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CannotRenewLicenseException.class)
    public ResponseEntity<AppError> handleCannotRenewLicenseException(CannotRenewLicenseException ex) {
        AppError error = new AppError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<AppError> handleDeviceNotFoundException(DeviceNotFoundException ex) {
        AppError error = new AppError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AppError> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        AppError error = new AppError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(LicenseActivationException.class)
    public ResponseEntity<AppError> handleLicenseActivationException(LicenseActivationException ex) {
        AppError error = new AppError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LicenseNotFoundException.class)
    public ResponseEntity<AppError> handleLicenseNotFoundException(LicenseNotFoundException ex) {
        AppError error = new AppError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LicenseTypeNotFoundException.class)
    public ResponseEntity<AppError> handleLicenseTypeNotFoundException(LicenseTypeNotFoundException ex) {
        AppError error = new AppError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<AppError> handlePasswordMismatchException(PasswordMismatchException ex) {
        AppError error = new AppError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<AppError> handleProductNotFoundException(ProductNotFoundException ex) {
        AppError error = new AppError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AppError> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        AppError error = new AppError(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppError> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        AppError error = new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
