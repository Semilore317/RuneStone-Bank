package com.abraham_bankole.runestone_bank.email.listener

import com.abraham_bankole.runestone_bank.common.event.StatementReadyEvent
import com.abraham_bankole.runestone_bank.common.event.TransactionCompletedEvent
import com.abraham_bankole.runestone_bank.common.event.UserLoginEvent
import com.abraham_bankole.runestone_bank.common.event.UserRegisteredEvent
import com.abraham_bankole.runestone_bank.common.kafka.KafkaTopics
import com.abraham_bankole.runestone_bank.email.dto.EmailDetails
import com.abraham_bankole.runestone_bank.email.service.EmailService
import jakarta.mail.MessagingException
import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.io.File

@Component
open class EmailEventListener(
    private val emailService: EmailService,
    private val javaMailSender: JavaMailSender
) {
    private val log = LoggerFactory.getLogger(EmailEventListener::class.java)

    // this only fires after the transaction was successful3
    @KafkaListener(topics = [KafkaTopics.USER_REGISTERED], groupId = "email-service")
    fun handleUserRegistered(event: UserRegisteredEvent) {
        val details = EmailDetails().apply {
            subject = "Welcome to Runestone Bank!"
            messageBody = String.format("Hello ${event.firstName}, welcome to your new account.", event.firstName)
            recipientName = event.firstName()
            recipientEmail = event.email()
        }

        emailService.sendEmailAlert(details)
    }

    @KafkaListener(topics = [KafkaTopics.USER_LOGIN], groupId = "email-service")
    fun handleUserLogin(event: UserLoginEvent) {
        val details = EmailDetails().apply {
            subject = "Runestone Bank Login Alert"
            messageBody = String.format("Hello ${event.name}, a new login was detected on your account.", event.name)
            recipientEmail = event.email()
            recipientName = event.name()
        }

        emailService.sendEmailAlert(details)
    }

    @KafkaListener(topics = [KafkaTopics.TRANSACTION_COMPLETED], groupId = "email-service")
    fun handleTransactionCompleted(event: TransactionCompletedEvent) {
        // debit alert to sender
        val debitAlert = EmailDetails().apply {
            subject = "DEBIT ALERT"
            recipientName = event.senderName()
            recipientEmail = event.senderEmail()
            messageBody = "You have successfully sent the sum of ${event.amount()} to ${event.receiverName} and your account has been debited."

        }

        // credit alert to receiver
        emailService.sendEmailAlert(EmailDetails().apply {
            subject = "DEBIT ALERT"
            recipientName = event.senderName()
            recipientEmail  = event.senderEmail()
            messageBody = "The sum of ${event.amount()} has been sent to your account from ${event.senderName()}."
        })

        emailService.sendEmailAlert(debitAlert);
    }

    @KafkaListener(topics = [KafkaTopics.STATEMENT_READY], groupId = "email-service")
    open fun handleStatementReady(event: StatementReadyEvent) {
        try {
            val mimeMessage = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, true)

            helper.setFrom("no-reply@runestonebank.com")
            helper.setTo(event.recipientEmail)
            helper.setSubject("RuneStone Bank Statement")
            helper.setText("Dear Customer,\n\nPlease find attached your requested account statement.\n\nRegards,\nRuneStone Bank")

            val file = FileSystemResource(File(event.filePath))
            helper.addAttachment(event.fileName, file)

            javaMailSender.send(mimeMessage)
            log.info("Statement email sent successfully to {}", event.recipientEmail)
        } catch (e: MessagingException) {
            log.error("Failed to send statement email to {}", event.recipientEmail, e)
            throw RuntimeException("Failed to send statement email", e)
        }
    }
}
