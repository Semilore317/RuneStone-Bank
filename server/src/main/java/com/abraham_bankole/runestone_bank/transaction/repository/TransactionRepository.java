package com.abraham_bankole.runestone_bank.transaction.repository;

import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
  List<Transaction> findByAccountNumberAndTimeOfCreationBetween(
      String accountNumber, LocalDateTime start, LocalDateTime end);

  List<Transaction> findByAccountNumberOrderByTimeOfCreationDesc(String accountNumber);
}
