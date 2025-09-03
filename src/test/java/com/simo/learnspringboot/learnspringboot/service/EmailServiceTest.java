package com.simo.learnspringboot.learnspringboot.service;

import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    JavaMailSender mailSender;

    @InjectMocks
    EmailService emailService;

    @Test
    void testSendResetPasswordEmail() throws Exception {
        // Arrange
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String recipient = "test@example.com";
        String token = "dummy-token-123";

        // Act
        emailService.sendResetPasswordEmail(recipient, token);

        // Capture
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();

        // Assert recipient & subject
        assertThat(sentMessage.getAllRecipients()[0].toString()).isEqualTo(recipient);
        assertThat(sentMessage.getSubject()).isEqualTo("🔐 Password Reset Request");

        // Extract email body safely
        String htmlContent = extractContent(sentMessage.getContent());

        // Assert HTML content
        assertThat(htmlContent).contains("dummy-token-123");
        assertThat(htmlContent).contains("Reset Password");
        assertThat(htmlContent).contains("http://localhost:8080/api/auth/reset-password?token=dummy-token-123");
    }

    private String extractContent(Object content) throws Exception {
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String result = extractContent(bodyPart.getContent());
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @Test
    void testSendPasswordResetEmail_Failure() {
        doThrow(new MailSendException("Failed to connect"))
                .when(mailSender).send(any(MimeMessage.class));

        when(mailSender.createMimeMessage()).thenReturn(new JavaMailSenderImpl().createMimeMessage());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            emailService.sendResetPasswordEmail("test@gmail.com", "token-123");
        });

        assertThat(thrown.getMessage()).isEqualTo("Failed to send email");
        assertThat(thrown.getCause()).isInstanceOf(MailSendException.class);
        assertThat(thrown.getCause().getMessage()).isEqualTo("Failed to connect");
    }
}
