package com.abraham_bankole.runestone_bank.service;

import com.abraham_bankole.runestone_bank.dto.*;

public interface UserService {
  BankResponse login(LoginDto loginDto);

  BankResponse createAccount(UserRequest userRequest);

  BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);

  BankResponse nameEnquiry(EnquiryRequest enquiryRequest);
}
