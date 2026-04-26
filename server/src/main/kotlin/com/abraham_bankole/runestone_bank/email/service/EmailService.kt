package com.abraham_bankole.runestone_bank.email.service

import com.abraham_bankole.runestone_bank.email.dto.EmailDetails
import org.springframework.scheduling.annotation.Async

interface EmailService {
  @Async fun sendEmailAlert(emailDetails: EmailDetails?)
}
