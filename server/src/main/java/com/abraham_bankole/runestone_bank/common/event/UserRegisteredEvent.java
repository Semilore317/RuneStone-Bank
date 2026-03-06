package com.abraham_bankole.runestone_bank.common.event;

// A lightweight record carrying only the exact data the email service needs
public record UserRegisteredEvent(Long userId, String email, String firstName) {}