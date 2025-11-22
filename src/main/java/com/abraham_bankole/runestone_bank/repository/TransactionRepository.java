package com.abraham_bankole.runestone_bank.repository;

import com.abraham_bankole.runestone_bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountNumberAndTimeOfCreationBetween(String accountNumber, LocalDate start, LocalDate end);
}
