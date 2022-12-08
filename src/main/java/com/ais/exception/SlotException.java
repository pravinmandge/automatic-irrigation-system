package com.ais.exception;

import com.ais.utils.IrrigationError;
import lombok.Getter;

@Getter
public class SlotException extends IrrigationException {

    public SlotException(IrrigationError irrigationError) {
        super(irrigationError);
    }
}
