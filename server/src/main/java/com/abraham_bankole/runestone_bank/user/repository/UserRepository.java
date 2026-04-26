package com.abraham_bankole.runestone_bank.user.repository;

import com.abraham_bankole.runestone_bank.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);

  boolean existsByAccountNumber(String accountNumber);

  User findByAccountNumber(String accountNumber);

  Optional<User> findByEmail(String email);
}
