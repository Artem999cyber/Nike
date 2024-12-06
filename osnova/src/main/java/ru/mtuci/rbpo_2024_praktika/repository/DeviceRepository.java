package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.Device;

import java.util.UUID;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Optional<Device> findByNameAndMacAddressAndUser(String name, String macAddress, ApplicationUser user);
}
