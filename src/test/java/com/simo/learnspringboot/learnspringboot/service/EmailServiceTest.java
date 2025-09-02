package com.simo.learnspringboot.learnspringboot.service;

import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void testSendResetPasswordEmail() throws Exception {
        // Arrange
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailService emailService = new EmailService(mailSender);

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
        } else if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
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
}
