package com.abraham_bankole.runestone_bank.transaction.repository;

import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
  List<Transaction> findByAccountNumberAndTimeOfCreationBetween(
      String accountNumber, LocalDate start, LocalDate end);
}
