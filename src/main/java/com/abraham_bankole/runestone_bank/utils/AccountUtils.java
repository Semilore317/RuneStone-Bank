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

    public static String generateAccountNumber() {
        Year currentYear = Year.now();

        int min = 100000;
        int max = 999999;

        int randomNumber = (int) Math.floor((Math.random() * (max - min + 1) + min));

        String year = String.valueOf(currentYear);

        return year + randomNumber;
    }
}
