package com.abraham_bankole.runestone_bank.service;

import com.abraham_bankole.runestone_bank.dto.TransactionDto;

public interface TransactionService {
    void saveTransaction(TransactionDto transaction);

}
