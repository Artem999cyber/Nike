package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationRole;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<ApplicationRole, UUID> {
    Optional<ApplicationRole> findByName(String name);
}
