package com.abraham_bankole.runestone_bank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @CreationTimestamp
    private LocalDate timeOfCreation;

    @UpdateTimestamp
    private LocalDate timeOfUpdate;
}
