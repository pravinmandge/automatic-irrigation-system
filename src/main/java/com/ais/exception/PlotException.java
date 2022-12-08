package com.ais.exception;

import com.ais.utils.IrrigationError;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PlotException extends IrrigationException {

    public PlotException(IrrigationError error) {
        super(error);
    }
}
