package com.ais.exception;

import com.ais.utils.ExceptionUtil;
import com.ais.utils.IrrigationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler({PlotException.class, SlotException.class, PlotIrrigationException.class})
    public ResponseEntity<ErrorResponse> handleIrrigationException(IrrigationException ex) {
        IrrigationError irrigationError = ex.getIrrigationError();
        return new ResponseEntity<>(new ErrorResponse(irrigationError.getErrorCode(), irrigationError.getDescription()), irrigationError.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleOtherException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ConstraintViolationException.class,
            MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {
        if (ex instanceof ConstraintViolationException) {
            return new ResponseEntity<>(ExceptionUtil.toErrorResponse((ConstraintViolationException) ex), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(ExceptionUtil.toErrorResponse((MethodArgumentNotValidException) ex), HttpStatus.BAD_REQUEST);
        }
    }
}