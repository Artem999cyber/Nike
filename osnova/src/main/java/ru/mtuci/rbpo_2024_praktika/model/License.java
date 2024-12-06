package ru.mtuci.rbpo_2024_praktika.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "licenses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class License {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user_id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product_id;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private LicenseType type_id;

    private Date first_activation_date;
    private Date ending_date;
    private boolean Blocked;
    private int device_count;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private ApplicationUser owner_id;

    private Duration duration;
    private String description;


}
