package com.ais.exception;

import com.ais.utils.IrrigationError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SlotException extends IrrigationException {

    public SlotException(IrrigationError irrigationError) {
        super(irrigationError);
    }
}
