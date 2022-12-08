package com.ais.exception;

import com.ais.utils.IrrigationError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IrrigationException extends RuntimeException {

    private final IrrigationError irrigationError;

    public IrrigationException(IrrigationError irrigationError) {
        super(irrigationError.getDescription());
        this.irrigationError = irrigationError;
    }

    public IrrigationException(IrrigationError irrigationError, Throwable cause) {
        super(irrigationError.getDescription(), cause);
        this.irrigationError = irrigationError;
    }
}
