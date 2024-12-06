package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_2024_praktika.model.LicenseType;

import java.util.Optional;
import java.util.UUID;

public interface LicenseTypeRepository extends JpaRepository<LicenseType, UUID> {
}
