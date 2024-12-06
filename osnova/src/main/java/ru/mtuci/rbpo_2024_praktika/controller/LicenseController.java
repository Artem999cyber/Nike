package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.dto.LicenseActivationRequestDto;
import ru.mtuci.rbpo_2024_praktika.dto.LicenseCreationRequestDto;
import ru.mtuci.rbpo_2024_praktika.dto.Ticket;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.service.UserService;
import ru.mtuci.rbpo_2024_praktika.service.licenses.LicenseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LicenseController {
    private final LicenseService licenseService;
    private final UserService userService;
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping ("/admin/licenses")
    public ResponseEntity<?> createLicense(@RequestBody LicenseCreationRequestDto license){
        return new ResponseEntity<>(licenseService.createLicense(license), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/licenses/activate")
    public ResponseEntity<Ticket> activateLicense(@RequestBody LicenseActivationRequestDto activationRequest,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        ApplicationUser user = userService.findByUsername(userDetails.getUsername()).get();

        Ticket ticket = licenseService.activateLicense(activationRequest, user);

        return ResponseEntity.ok(ticket);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/licenses")
    public ResponseEntity<?> getLicenses(@RequestBody LicenseActivationRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetails userDetails){
        ApplicationUser user = userService.findByUsername(userDetails.getUsername()).get();

        List<Ticket> tickets = licenseService.getLicensesInfo(requestDto,user);

        return ResponseEntity.ok(tickets);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/licenses/renew")
    public ResponseEntity<Ticket> renewLicense(@RequestBody String code){
        Ticket ticket = licenseService.renewLicense(code);
        return ResponseEntity.ok(ticket);
    }
}
