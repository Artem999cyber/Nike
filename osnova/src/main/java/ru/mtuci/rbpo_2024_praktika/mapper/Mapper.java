package ru.mtuci.rbpo_2024_praktika.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.mtuci.rbpo_2024_praktika.dto.LicenseActivationRequestDto;
import ru.mtuci.rbpo_2024_praktika.model.Device;

@Component
@RequiredArgsConstructor
public class Mapper {
    private final ModelMapper mapper;

    public Device toDevice(LicenseActivationRequestDto activationRequestDto){
        return mapper.map(activationRequestDto, Device.class);
    }
}
