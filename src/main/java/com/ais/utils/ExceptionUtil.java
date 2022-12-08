package com.ais.utils;

import com.ais.exception.ErrorResponse;
import com.ais.exception.FieldError;
import lombok.val;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExceptionUtil {

    public static ErrorResponse toErrorResponse(ConstraintViolationException exception) {
        val fieldErrors = exception.getConstraintViolations().stream().map(v -> new FieldError(
                v.getPropertyPath().toString(),
                v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                v.getMessage()
        )).collect(Collectors.toList());
        val message = exception.getConstraintViolations().stream().map(v -> v.getPropertyPath() + ":" + v.getMessage()).collect(Collectors.joining(", "));
        return new ErrorResponse(
                IrrigationError.VALIDATION_ERROR.getErrorCode(),
                message,
                fieldErrors
        );
    }

    public static ErrorResponse toErrorResponse(MethodArgumentNotValidException exception) {
        val fieldErrors = exception.getFieldErrors().stream().map(e -> new FieldError(e.getField(), Optional.ofNullable(e.getCode()).orElse(FieldError.INVALID_FORMAT), Optional.ofNullable(e.getDefaultMessage()).orElse("is invalid")))
        .collect(Collectors.toList());

        val globalErrorList = exception.getGlobalErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        String message = null;
        if(globalErrorList.isEmpty()) {
            message = fieldErrors.stream().map(FieldError::toString).collect(Collectors.joining(", "));
        } else {
            message = String.join(", ", globalErrorList);
        }
        return new ErrorResponse(
                IrrigationError.VALIDATION_ERROR.getErrorCode(),
                message,
                fieldErrors
        );
    }
}
