package com.abraham_bankole.runestone_bank.common.event;

/**
 * Published after a bank statement PDF has been generated,
 * so the email domain can send it as an attachment without the
 * statement domain depending on email infrastructure.
 */
public record StatementReadyEvent(String recipientEmail, String filePath, String fileName) {}
