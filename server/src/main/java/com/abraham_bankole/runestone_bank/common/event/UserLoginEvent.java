package com.abraham_bankole.runestone_bank.common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserLoginEvent(
        @JsonProperty("name") String name,
        @JsonProperty("email") String email
) {
    @JsonCreator
    public UserLoginEvent {
    }
}
