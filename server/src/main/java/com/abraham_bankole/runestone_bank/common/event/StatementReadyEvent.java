package com.abraham_bankole.runestone_bank.common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Published after a bank statement PDF has been generated,
 * so the email domain can send it as an attachment without the
 * statement domain depending on email infrastructure.
 */
public record StatementReadyEvent(
        @JsonProperty("recipientEmail") String recipientEmail,
        @JsonProperty("filePath") String filePath,
        @JsonProperty("fileName") String fileName
) {
    @JsonCreator
    public StatementReadyEvent {
    }
}
