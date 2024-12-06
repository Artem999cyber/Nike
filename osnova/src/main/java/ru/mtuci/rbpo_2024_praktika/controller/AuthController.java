package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.dto.JwtRequestDto;
import ru.mtuci.rbpo_2024_praktika.dto.JwtResponseDto;
import ru.mtuci.rbpo_2024_praktika.dto.RegistrationUserDto;
import ru.mtuci.rbpo_2024_praktika.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponseDto createAuthToken(@RequestBody JwtRequestDto request) {
        return authService.createAuthToken(request);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public JwtResponseDto createUser(@RequestBody RegistrationUserDto registrationUserDto) {
        return authService.createUser(registrationUserDto);
    }
}
