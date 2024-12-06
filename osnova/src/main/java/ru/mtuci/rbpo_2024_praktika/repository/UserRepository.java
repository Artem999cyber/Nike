package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationRole;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<ApplicationUser, UUID> {
    Optional<ApplicationUser> findByUsername(String username);
}
