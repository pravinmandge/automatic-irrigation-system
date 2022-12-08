package com.ais.exception;

import com.ais.utils.IrrigationError;
import lombok.Getter;

@Getter
public class PlotIrrigationException extends IrrigationException {

    public PlotIrrigationException(IrrigationError error) {
        super(error);
    }
}
