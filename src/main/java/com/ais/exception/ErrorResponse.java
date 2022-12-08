package com.ais.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String errorCode;
    private String errorDescription;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FieldError> fieldErrors ;

    public ErrorResponse(String errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.fieldErrors = Collections.emptyList();
    }
}
