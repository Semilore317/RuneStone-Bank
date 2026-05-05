package com.abraham_bankole.runestone_bank.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    ACCOUNT_EXISTS ("001", "This user email exists"),
    ACCOUNT_CREATION_SUCCESS("002", "Account has been successfully created"),
    ACCOUNT_NOT_EXIST ("003", "Account does not exist"),
    ACCOUNT_FOUND("004", "Account has been found"),
    ACCOUNT_CREDITED("005", "Account has been successfully credited"),
    INSUFFICIENT_BALANCE("006", "Insufficient balance"),
    ACCOUNT_DEBITED("007", "Account debited"),
    TRANSACTION_SUCCESSFUL ("008", "Transaction Successful"),
    LOGIN_SUCCESS("009", "Login Successful"),
    PROFILE_UPDATE_SUCCESS("010", "Profile Updated Successfully"),
    PASSWORD_UPDATE_SUCCESS("011", "Password Updated Successfully"),
    PASSWORD_INCORRECT("012", "Password Incorrect");

    private final String code;
    private final String message;
}
