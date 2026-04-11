package com.abraham_bankole.runestone_bank.common.exception;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BankResponse handleBadCredentials(BadCredentialsException e) {
        return BankResponse.builder()
                .responseCode("401")
                .responseMessage("Invalid email or password")
                .build();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BankResponse handleUsernameNotFound(UsernameNotFoundException e) {
        return BankResponse.builder()
                .responseCode("401")
                .responseMessage("User not found")
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BankResponse handleRuntimeException(RuntimeException e) {
        return BankResponse.builder()
                .responseCode("500")
                .responseMessage("Internal server error: " + e.getMessage())
                .build();
    }
}
