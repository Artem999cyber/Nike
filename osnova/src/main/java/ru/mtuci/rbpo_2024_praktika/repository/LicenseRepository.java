package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.Device;
import ru.mtuci.rbpo_2024_praktika.model.License;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LicenseRepository extends JpaRepository<License, UUID> {
    Optional<License> findLicenseByCode(String code);

    @Query("SELECT l FROM License l " +
            "WHERE l.owner_id = :user " +
            "AND l.Blocked = false " +
            "AND l.device_count > 0 " +
            "AND :device MEMBER OF l.user_id.devices " +
            "AND (l.ending_date IS NULL OR l.ending_date > CURRENT_DATE)")
    List<License> findByDeviceAndUserAndNotBlocked(@Param("device") Device device,
                                                   @Param("user") ApplicationUser user);
}
