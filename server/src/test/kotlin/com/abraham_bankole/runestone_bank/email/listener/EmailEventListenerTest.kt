package com.abraham_bankole.runestone_bank.email.listener

import com.abraham_bankole.runestone_bank.common.event.StatementReadyEvent
import com.abraham_bankole.runestone_bank.common.event.UserLoginEvent
import com.abraham_bankole.runestone_bank.common.event.UserRegisteredEvent
import com.abraham_bankole.runestone_bank.email.dto.EmailDetails
import com.abraham_bankole.runestone_bank.email.service.EmailService
import jakarta.mail.internet.MimeMessage
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.javamail.JavaMailSender

@ExtendWith(MockitoExtension::class)
class EmailEventListenerTest {

  @Mock private lateinit var emailService: EmailService

  @Mock private lateinit var javaMailSender: JavaMailSender

  @InjectMocks private lateinit var emailEventListener: EmailEventListener

  @TempDir lateinit var tempDir: Path

  @Test
  fun `handleUserRegistered sends email`() {
    val event = UserRegisteredEvent(1L, "test@test.com", "John")
    emailEventListener.handleUserRegistered(event)
    verify(emailService, times(1)).sendEmailAlert(any(EmailDetails::class.java))
  }

  @Test
  fun `handleUserLogin sends email`() {
    val event = UserLoginEvent("John", "test@test.com")
    emailEventListener.handleUserLogin(event)
    verify(emailService, times(1)).sendEmailAlert(any(EmailDetails::class.java))
  }

  @Test
  fun `handleStatementReady sends mime message`() {
    val fileName = "statement_january.pdf"
    val tempFile = tempDir.resolve(fileName)
    Files.writeString(tempFile, "fake pdf content") // Actually creates the file on disk

    val event = StatementReadyEvent("test@test.com", tempFile.toAbsolutePath().toString(), fileName)

    val mimeMessage = mock(MimeMessage::class.java)
    `when`(javaMailSender.createMimeMessage()).thenReturn(mimeMessage)

    emailEventListener.handleStatementReady(event)

    verify(javaMailSender, times(1)).send(mimeMessage)
  }
}
