package com.abraham_bankole.runestone_bank.service.impl;

import com.abraham_bankole.runestone_bank.dto.TransactionDto;

import com.abraham_bankole.runestone_bank.entity.Transaction;
import com.abraham_bankole.runestone_bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("SUCCESS") // not really sure what scenarios would warrant anything other than this...
                .build();
        transactionRepository.save(transaction);
        System.out.println("Transaction Saved Successfully!");
    }
}
