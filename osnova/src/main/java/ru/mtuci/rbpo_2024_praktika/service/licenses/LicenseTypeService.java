package ru.mtuci.rbpo_2024_praktika.service.licenses;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.exceptions.LicenseTypeNotFoundException;
import ru.mtuci.rbpo_2024_praktika.model.LicenseType;
import ru.mtuci.rbpo_2024_praktika.repository.LicenseTypeRepository;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LicenseTypeService {
   private final LicenseTypeRepository licenseTypeRepository;

   public LicenseType getLicenseTypeByID(UUID licenseTypeId){
      return licenseTypeRepository.findById(licenseTypeId).orElseThrow(()-> new LicenseTypeNotFoundException("License type with ID "+licenseTypeId + " not found."));
   }
}

