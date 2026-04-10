package com.abraham_bankole.runestone_bank.common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Published after a transfer completes so the email domain can send debit/credit alerts
 * without the transaction domain depending on email infrastructure.
 */
public record TransactionCompletedEvent(
        @JsonProperty("senderAccountNumber") String senderAccountNumber,
        @JsonProperty("senderName") String senderName,
        @JsonProperty("senderEmail") String senderEmail,
        @JsonProperty("receiverAccountNumber") String receiverAccountNumber,
        @JsonProperty("receiverName") String receiverName,
        @JsonProperty("receiverEmail") String receiverEmail,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("transactionType") String transactionType  // "CREDIT", "DEBIT", or "TRANSFER"
) {
    @JsonCreator
    public TransactionCompletedEvent {
    }
}
