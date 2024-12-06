package ru.mtuci.rbpo_2024_praktika.service.licenses;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.dto.LicenseActivationRequestDto;
import ru.mtuci.rbpo_2024_praktika.dto.LicenseCreationRequestDto;
import ru.mtuci.rbpo_2024_praktika.dto.Ticket;
import ru.mtuci.rbpo_2024_praktika.exceptions.CannotRenewLicenseException;
import ru.mtuci.rbpo_2024_praktika.exceptions.LicenseActivationException;
import ru.mtuci.rbpo_2024_praktika.exceptions.LicenseNotFoundException;
import ru.mtuci.rbpo_2024_praktika.mapper.Mapper;
import ru.mtuci.rbpo_2024_praktika.model.*;
import ru.mtuci.rbpo_2024_praktika.repository.LicenseRepository;
import ru.mtuci.rbpo_2024_praktika.service.UserService;


import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final ProductService productService;
    private final UserService userService;
    private final LicenseTypeService licenseTypeService;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceService deviceService;
    private final TicketService ticketService;
    private final Mapper mapper;
    public License createLicense(LicenseCreationRequestDto licenseDto){
        Product product = productService.getproductById(licenseDto.productId());

        ApplicationUser owner = userService.findById(licenseDto.ownerId());

        LicenseType licenseType = licenseTypeService.getLicenseTypeByID(licenseDto.licenseTypeId());

        License license = new License();
        license.setProduct_id(product);
        license.setType_id(licenseType);
        license.setOwner_id(owner);
        license.setCode(UUID.randomUUID().toString());
        license.setDuration(licenseDto.duration());
        license.setDevice_count(license.getDevice_count());
        license.setBlocked(false);
        license.setDescription(licenseDto.description());
        String historyDescription = "A new license was created at " + new Date() + "by user with ID" + licenseDto.ownerId().toString();
        licenseHistoryService.recordLicenseChange(license,owner,"Created",historyDescription);
        return license;
    }

    public Ticket activateLicense(LicenseActivationRequestDto activationRequest, ApplicationUser user){
        Device device = deviceService.registerOrUpdateDevice(mapper.toDevice(activationRequest), user);

        License license = licenseRepository.findLicenseByCode(activationRequest.code())
                .orElseThrow(() -> new LicenseNotFoundException("License not found for the provided code."));

        validateActivation(license, user);

        deviceService.createDeviceLicense(license, device);

        updateLicense(license);

        licenseHistoryService.recordLicenseChange(license, user, "Activated", "License successfully activated.");

        return ticketService.generateTicket(license,device);
    }

    private void updateLicense(License license){
        license.setBlocked(false);
        license.setFirst_activation_date(new Date());
        Instant activationInstant = Instant.now();
        Instant expirationInstant = activationInstant.plus(license.getDuration());
        license.setEnding_date(Date.from(expirationInstant));
        licenseRepository.save(license);
    }

    public List<Ticket> getLicensesInfo(LicenseActivationRequestDto requestDto, ApplicationUser user){
        Device device = deviceService.findDeviceByInfo(requestDto.deviceName(),requestDto.mac_address(),user);
        List<License> activeLicenses = getActiveLicensesForDevice(device, device.getUser());

        if(activeLicenses.isEmpty())
            throw new LicenseNotFoundException("Licenses for this device not found.");

        return activeLicenses.stream().map(l -> ticketService.generateTicket(l,device)).toList();
    }

    private List<License> getActiveLicensesForDevice(Device device, ApplicationUser user) {
        return licenseRepository.findByDeviceAndUserAndNotBlocked(device, user);
    }

    public Ticket renewLicense(String code){
        License license = findLicenseByCode(code);
        if(license.getEnding_date()==null || license.getEnding_date().before(new Date()))
            throw new CannotRenewLicenseException("Cannot renew expired license.");
        Instant previousEnding = license.getEnding_date().toInstant();
        Instant newEnding = previousEnding.plusMillis(license.getDuration().toMillis());
        license.setEnding_date(Date.from(newEnding));
        licenseRepository.save(license);
        return ticketService.generateTicket(license);
    }

    public License findLicenseByCode(String code){
        return licenseRepository.findLicenseByCode(code)
                .orElseThrow(()-> new LicenseNotFoundException("License with this code not found."));
    }
    private void validateActivation(License license, ApplicationUser user) {
        if (license.isBlocked()) {
            throw new LicenseActivationException("Activation failed: the license is blocked.");
        }

        if (!license.getId().equals(user.getId())) {
            throw new LicenseActivationException("Activation failed: the license belongs to another user.");
        }

        if (license.getFirst_activation_date() != null) {
            if(license.getEnding_date().before(new Date())){
                throw new LicenseActivationException("Activation failed: the license has expired.");
            } else{
                throw new LicenseActivationException("Activation failed: the license already activated.");
            }
        }
    }

}
