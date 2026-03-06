package com.abraham_bankole.runestone_bank.user.service;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.user.dto.EnquiryRequest;
import com.abraham_bankole.runestone_bank.user.dto.UserRequest;

public interface UserService {

  BankResponse createAccount(UserRequest userRequest);

  BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);

  BankResponse nameEnquiry(EnquiryRequest enquiryRequest);
}
