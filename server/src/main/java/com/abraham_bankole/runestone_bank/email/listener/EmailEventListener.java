package com.abraham_bankole.runestone_bank.email.listener;

import com.abraham_bankole.runestone_bank.common.event.StatementReadyEvent;
import com.abraham_bankole.runestone_bank.common.event.TransactionCompletedEvent;
import com.abraham_bankole.runestone_bank.common.event.UserLoginEvent;
import com.abraham_bankole.runestone_bank.common.event.UserRegisteredEvent;
import com.abraham_bankole.runestone_bank.email.dto.EmailDetails;
import com.abraham_bankole.runestone_bank.email.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {
    private final EmailService emailService;
    private final JavaMailSender javaMailSender;

    // this only fires after the transaction was successful
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegisteredEvent event) {
        String subject = "Welcome to Runestone Bank!";
        String body = String.format("Hello %s, welcome to your new account.", event.firstName());
        String name =  event.firstName();
        String mail = event.email();

        EmailDetails details = new EmailDetails();
        details.setSubject(subject);
        details.setMessageBody(body);
        details.setRecipientName(name);
        details.setRecipientEmail(mail);
        emailService.sendEmailAlert(details);
    }

    @Async
    @EventListener // NOT a TransactionalEventListener
    public void handleUserLogin(UserLoginEvent event) {
        EmailDetails details = new EmailDetails();
        details.setSubject("Runestone Bank Login Alert");
        details.setMessageBody(String.format("Hello %s, a new login was detected on your account.", event.name()));
        details.setRecipientEmail(event.email());
        details.setRecipientName(event.name());

        emailService.sendEmailAlert(details);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        // debit alert to sender
        EmailDetails debitAlert = new EmailDetails();
        debitAlert.setSubject("DEBIT ALERT");
        debitAlert.setRecipientName(event.senderName());
        debitAlert.setRecipientEmail(event.senderEmail());
        debitAlert.setMessageBody(String.format(
                "You have successfully sent the sum of $%s to %s and your account has been debited.",
                event.amount(), event.receiverName()));
        emailService.sendEmailAlert(debitAlert);

        // credit alert to receiver
        EmailDetails creditAlert = new EmailDetails();
        creditAlert.setSubject("CREDIT ALERT");
        creditAlert.setRecipientName(event.receiverName());
        creditAlert.setRecipientEmail(event.receiverEmail());
        creditAlert.setMessageBody(String.format(
                "The sum of $%s has been sent to your account from %s.",
                event.amount(), event.senderName()));
        emailService.sendEmailAlert(creditAlert);
    }

    @Async
    @EventListener
    public void handleStatementReady(StatementReadyEvent event) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("no-reply@runestonebank.com");
            helper.setTo(event.recipientEmail());
            helper.setSubject("RuneStone Bank Statement");
            helper.setText("Dear Customer,\n\nPlease find attached your requested account statement.\n\nRegards,\nRuneStone Bank");

            FileSystemResource file = new FileSystemResource(new File(event.filePath()));
            helper.addAttachment(event.fileName(), file);

            javaMailSender.send(mimeMessage);
            log.info("Statement email sent successfully to {}", event.recipientEmail());
        } catch (MessagingException e) {
            log.error("Failed to send statement email to {}", event.recipientEmail(), e);
            throw new RuntimeException("Failed to send statement email", e);
        }
    }
}
