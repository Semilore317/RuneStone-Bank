package com.abraham_bankole.runestone_bank.transaction.entity;

import com.abraham_bankole.runestone_bank.common.enums.TransactionStatus;
import com.abraham_bankole.runestone_bank.common.enums.TransactionType;
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

  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;

  private BigDecimal amount;
  private String accountNumber;

  @Enumerated(EnumType.STRING)
  private TransactionStatus status; // PENDING, FAILED, SUCCESS

  private String counterpartyAccountNumber;
  private String counterpartyName;

  @CreationTimestamp private LocalDateTime timeOfCreation;

  @UpdateTimestamp private LocalDateTime timeOfUpdate;
}
