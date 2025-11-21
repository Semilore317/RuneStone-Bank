package com.abraham_bankole.runestone_bank.repository;

import com.abraham_bankole.runestone_bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

}
