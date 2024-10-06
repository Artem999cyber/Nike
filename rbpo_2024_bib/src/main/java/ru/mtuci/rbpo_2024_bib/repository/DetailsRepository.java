 package ru.mtuci.rbpo_2024_bib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.rbpo_2024_bib.model.Details;

@Repository
public interface DetailsRepository extends JpaRepository<Details, Long> {
}
