package ru.mtuci.rbpo_2024_praktika.dto;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;

}
