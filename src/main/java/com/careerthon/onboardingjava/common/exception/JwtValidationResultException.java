package com.careerthon.onboardingjava.common.exception;

public class JwtValidationResultException extends RuntimeException {
    public JwtValidationResultException(String message) {
        super(message);
    }
    public JwtValidationResultException(String message, Throwable e) {
        super(message, e);
    }
}
