package com.ais.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldError {

    public static final String INVALID_FORMAT = "InvalidFormat";
    public static final String REQUIRED = "NotNull";

    private String field;
    private String errorCode;
    private String errorDescription;

    @Override
    public String toString(){
        return field + ":" + errorDescription;
    }

}
