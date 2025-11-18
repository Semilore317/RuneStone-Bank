package com.abraham_bankole.runestone_bank.utils;

import java.time.Year;

// method to generate an account number
// quite primitive at the moment
// - doesnt take "buckets" into account, just creates a random 10 digit number
public class AccountUtils {
    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user email already exists";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "Account does not exist";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_CREDITED_SUCCESS = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Account has been credited successfully";
    public static final String ACCOUNT_FOUND_SUCCESS = "User account Found Successfully";

    public static String generateAccountNumber() {
        Year currentYear = Year.now();

        int min = 100000;
        int max = 999999;

        int randomNumber = (int) Math.floor((Math.random() * (max - min + 1) + min));

        String year = String.valueOf(currentYear);

        return year + randomNumber;
    }
}
