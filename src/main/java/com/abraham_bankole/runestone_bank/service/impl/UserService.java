package com.abraham_bankole.runestone_bank.service.impl;

import com.abraham_bankole.runestone_bank.dto.BankResponse;
import com.abraham_bankole.runestone_bank.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.dto.EnquiryRequest;
import com.abraham_bankole.runestone_bank.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest) ;
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) ;
    BankResponse nameEnquiry(EnquiryRequest enquiryRequest) ;
    BankResponse creditAccount(CreditDebitRequest request);
}
