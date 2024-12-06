package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_2024_praktika.model.DeviceLicense;

import java.util.UUID;

public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, UUID> {

}
