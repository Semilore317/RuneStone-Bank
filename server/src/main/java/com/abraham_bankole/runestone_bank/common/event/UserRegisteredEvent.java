package com.abraham_bankole.runestone_bank.common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// carries only the exact data the email service needs
public record UserRegisteredEvent(
        @JsonProperty("userId") Long userId,
        @JsonProperty("email") String email,
        @JsonProperty("firstName") String firstName
) {
    @JsonCreator
    public UserRegisteredEvent {
    }
}