package ru.mtuci.rbpo_2024_praktika.service.licenses;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.exceptions.DeviceNotFoundException;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.Device;
import ru.mtuci.rbpo_2024_praktika.model.DeviceLicense;
import ru.mtuci.rbpo_2024_praktika.model.License;
import ru.mtuci.rbpo_2024_praktika.repository.DeviceLicenseRepository;
import ru.mtuci.rbpo_2024_praktika.repository.DeviceRepository;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final DeviceLicenseRepository deviceLicenseRepository;

    public Device registerOrUpdateDevice(Device device, ApplicationUser user){
        device.setUser(user);
        return deviceRepository.save(device);
    }

    public DeviceLicense createDeviceLicense(License license, Device device){
        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setLicense_id(license);
        deviceLicense.setDevice_id(device);
        deviceLicense.setActivation_date(new Date());
        return deviceLicenseRepository.save(deviceLicense);
    }

    public Device findDeviceByInfo(String name, String macAddress, ApplicationUser user) {
        return deviceRepository.findByNameAndMacAddressAndUser(name, macAddress, user)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found."));
    }
}
