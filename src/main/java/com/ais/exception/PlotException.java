package com.ais.exception;

import com.ais.utils.IrrigationError;
import lombok.Getter;

@Getter
public class PlotException extends IrrigationException {

    public PlotException(IrrigationError error) {
        super(error);
    }
}
