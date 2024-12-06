package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_2024_praktika.model.LicenseHistory;

import java.util.UUID;

public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, UUID> {

}
