package ru.mtuci.rbpo_2024_praktika.dto;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

public record Ticket(Date date, Duration lifetime, Date activation_date, Date expiration_date, UUID user_id,
                     UUID device_id, boolean isLicenseBlocked, String signature) {
}
