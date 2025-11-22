package com.abraham_bankole.runestone_bank.service;

import com.abraham_bankole.runestone_bank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
