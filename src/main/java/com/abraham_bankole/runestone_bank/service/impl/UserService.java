package com.abraham_bankole.runestone_bank.service.impl;

import com.abraham_bankole.runestone_bank.dto.BankResponse;
import com.abraham_bankole.runestone_bank.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest) ;
}
