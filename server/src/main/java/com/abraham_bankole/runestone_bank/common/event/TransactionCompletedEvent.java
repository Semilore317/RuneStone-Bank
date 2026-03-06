package com.abraham_bankole.runestone_bank.common.event;

import java.math.BigDecimal;

/**
 * Published after a transfer completes so the email domain can send debit/credit alerts
 * without the transaction domain depending on email infrastructure.
 */
public record TransactionCompletedEvent(
    String senderAccountNumber,
    String senderName,
    String senderEmail,
    String receiverAccountNumber,
    String receiverName,
    String receiverEmail,
    BigDecimal amount,
    String transactionType  // "CREDIT", "DEBIT", or "TRANSFER"
) {}
