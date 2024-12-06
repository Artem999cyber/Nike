package ru.mtuci.rbpo_2024_praktika.service.licenses;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.License;
import ru.mtuci.rbpo_2024_praktika.model.LicenseHistory;
import ru.mtuci.rbpo_2024_praktika.repository.LicenseHistoryRepository;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class LicenseHistoryService {
    private final LicenseHistoryRepository licenseHistoryRepository;

    public void recordLicenseChange(License license, ApplicationUser owner, String status, String description) {
        LicenseHistory licenseHistory = new LicenseHistory();
        licenseHistory.setLicense_id(license);
        licenseHistory.setChange_date(new Date());
        licenseHistory.setUser_id(owner);
        licenseHistory.setStatus(status);
        licenseHistory.setDescription(description);
        licenseHistoryRepository.save(licenseHistory);
    }
}
