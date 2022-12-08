package com.ais.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum IrrigationError {

    // Plot
    PLOT_NOT_FOUND("Plot not found", HttpStatus.NOT_FOUND),
    UPDATE_PLOT_FAILED("Update plot failed", HttpStatus.INTERNAL_SERVER_ERROR),
    CREATE_PLOT_FAILED("Create plot failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Plot
    PLOT_IRRIGATION_NOT_FOUND("Plot irrigation not found", HttpStatus.NOT_FOUND),
    UPDATE_PLOT_IRRIGATION_FAILED("Update plot irrigation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    CREATE_PLOT_IRRIGATION_FAILED("Create plot irrigation failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Slot
    SLOT_NOT_FOUND("Slot not found", HttpStatus.NOT_FOUND),
    CREATE_SLOT_FAILED("Create slot failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Other
    INTERNAL_ERROR("Internal error while processing request", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR("Error while validating the request", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String description;
    private final HttpStatus httpStatus;

    IrrigationError(String description, HttpStatus httpStatus){
        this.errorCode = name();
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
