package ru.mtuci.rbpo_2024_praktika.exceptions;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
@JsonInclude(JsonInclude.Include.NON_NULL) // Опционально, если хотите скрыть null-значения
@Data
public class AppError {
    private int status;
    private String message;
    private Date timestamp;

    public AppError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }

}
