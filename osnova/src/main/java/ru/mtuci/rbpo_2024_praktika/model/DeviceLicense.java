package ru.mtuci.rbpo_2024_praktika.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "device_licenses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceLicense {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "license_id", nullable = false)
    private License license_id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device_id;

    private Date activation_date;
}
