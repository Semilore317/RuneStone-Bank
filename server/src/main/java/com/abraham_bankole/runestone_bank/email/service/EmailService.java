package com.abraham_bankole.runestone_bank.email.service;

import com.abraham_bankole.runestone_bank.email.dto.EmailDetails;

public interface EmailService {
  void sendEmailAlert(EmailDetails emailDetails);
}
