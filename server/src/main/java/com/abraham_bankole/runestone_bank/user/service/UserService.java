package com.abraham_bankole.runestone_bank.user.service;

import com.abraham_bankole.runestone_bank.user.dto.*;
import com.abraham_bankole.runestone_bank.transaction.dto.*;
import com.abraham_bankole.runestone_bank.common.dto.*;
import com.abraham_bankole.runestone_bank.email.dto.*;;

public interface UserService {
  BankResponse login(LoginDto loginDto);

  BankResponse createAccount(UserRequest userRequest);

  BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);

  BankResponse nameEnquiry(EnquiryRequest enquiryRequest);
}
