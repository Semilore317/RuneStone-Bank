package com.abraham_bankole.runestone_bank.email.service.impl

import com.abraham_bankole.runestone_bank.email.dto.EmailDetails
import com.abraham_bankole.runestone_bank.email.service.EmailService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
open class EmailServiceImpl(
  @param:Value("\${spring.mail.username}") private val senderEmail: String,
  @param:Qualifier("mailSender") private val javaMailSender: JavaMailSender,
) : EmailService {

  @Async
  override fun sendEmailAlert(emailDetails: EmailDetails?) {
    if (emailDetails == null) return

    try {
      // Using explicit setters to ensure build success. IntelliJ may warn about property access,
      // but the Kotlin 2.1.0 compiler currently rejects property assignment for these Spring
      // fields.
      val mailMessage =
        SimpleMailMessage().apply {
          setFrom(senderEmail)
          setTo(emailDetails.recipientEmail)
          setText(emailDetails.messageBody)
          setSubject(emailDetails.subject)
        }

      javaMailSender.send(mailMessage)
      println("Mail sent successfully!")
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }
}
