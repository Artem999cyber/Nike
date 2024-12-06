package ru.mtuci.rbpo_2024_praktika.dto;

import java.time.Duration;
import java.util.UUID;

public record LicenseCreationRequestDto(UUID productId, UUID ownerId, UUID licenseTypeId, Duration duration,
                                        int deviceCount, String description) {
}
