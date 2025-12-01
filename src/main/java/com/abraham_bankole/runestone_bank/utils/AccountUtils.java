package com.abraham_bankole.runestone_bank.utils;

import java.time.Year;

// method to generate an account number
// quite primitive at the moment
// - doesnt take "buckets" into account, just creates a random 10 digit number
public class AccountUtils {
    public static final String ACCOUNT_FOUND_SUCCESS = "User account Found Successfully";
    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user email exists";
    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "Account does not exist";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Account has been credited successfully";
    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance";
    public static final String ACCOUNT_DEBITED_SUCCESS_CODE = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been debited successfully!";
    public static final String TRANSACTION_SUCCESSFUL_CODE = "008";
    public static final String TRANSACTION_SUCCESSFUL_MESSAGE = "Transaction Successful";
    public static final String LOGIN_SUCCESS_CODE = "009";
    public static final String LOGIN_SUCCESS_MESSAGE = "Login Successfully";

    public static String generateAccountNumber() {
        Year currentYear = Year.now();

        int min = 100000;
        int max = 999999;

        int randomNumber = (int) Math.floor((Math.random() * (max - min + 1) + min));

        String year = String.valueOf(currentYear);

        return year + randomNumber;
    }
}
