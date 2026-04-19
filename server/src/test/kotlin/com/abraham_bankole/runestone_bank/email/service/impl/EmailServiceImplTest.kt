package com.abraham_bankole.runestone_bank.email.service.impl

import com.abraham_bankole.runestone_bank.email.dto.EmailDetails
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

@ExtendWith(MockitoExtension::class)
class EmailServiceImplTest {

    @Mock
    private lateinit var javaMailSender: JavaMailSender

    @Test
    fun `sendEmailAlert sends successfully when details are valid`() {
        // Arrange
        val emailService = EmailServiceImpl("sender@test.com", javaMailSender)
        val details = EmailDetails(
            recipientEmail = "receiver@test.com",
            messageBody = "Hello world",
            subject = "Test Subject",
            attachment = null
        )

        // Act
        emailService.sendEmailAlert(details)

        // Assert
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage::class.java))
    }

    @Test
    fun `sendEmailAlert throws runtime exception if javaMailSender fails`() {
        // Arrange
        val emailService = EmailServiceImpl("sender@test.com", javaMailSender)
        val details = EmailDetails("x", "x", "x", "x")

        doThrow(RuntimeException("Mail server down")).`when`(javaMailSender).send(any(SimpleMailMessage::class.java))

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            emailService.sendEmailAlert(details)
        }
    }
}
