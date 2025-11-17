package com.abraham_bankole.runestone_bank.service.impl;

import com.abraham_bankole.runestone_bank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
