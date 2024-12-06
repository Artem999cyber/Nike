package ru.mtuci.rbpo_2024_praktika.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.dto.JwtRequestDto;
import ru.mtuci.rbpo_2024_praktika.dto.JwtResponseDto;
import ru.mtuci.rbpo_2024_praktika.dto.RegistrationUserDto;
import ru.mtuci.rbpo_2024_praktika.exceptions.InvalidCredentialsException;
import ru.mtuci.rbpo_2024_praktika.exceptions.PasswordMismatchException;
import ru.mtuci.rbpo_2024_praktika.exceptions.UserAlreadyExistsException;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.utils.JwtUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public JwtResponseDto createAuthToken(JwtRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        UserDetails userDetails = userService.loadUserByUsername(request.username());
        String token = jwtUtils.generateToken(userDetails);
        return new JwtResponseDto(token);
    }

    public JwtResponseDto createUser(RegistrationUserDto registrationUserDto) {
        if (!registrationUserDto.password().equals(registrationUserDto.confirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        if (userService.findByUsername(registrationUserDto.username()).isPresent()) {
            throw new UserAlreadyExistsException("A user with the specified name already exists");
        }
        ApplicationUser user = userService.createNewUser(registrationUserDto);
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        String token = jwtUtils.generateToken(userDetails);
        return new JwtResponseDto(token);
    }
}
