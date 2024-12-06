package ru.mtuci.rbpo_2024_praktika.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "licenses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LicenseHistory {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "license_id", nullable = false)
    private License license_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user_id;

    private String status;
    private Date change_date;
    private String description;
}
