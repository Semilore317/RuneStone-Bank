package com.abraham_bankole.runestone_bank.transaction.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transaction")
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID) // 16-bit transaction id string
  private String transactionId;

  private String transactionType;
  private BigDecimal amount;
  private String accountNumber;
  private String status; // PENDING, FAILED, SUCCESS

  private String counterpartyAccountNumber;
  private String counterpartyName;

  @CreationTimestamp private LocalDateTime timeOfCreation;

  @UpdateTimestamp private LocalDateTime timeOfUpdate;
}
