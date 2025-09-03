package com.simo.learnspringboot.learnspringboot.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendResetPasswordEmail(String to, String token) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("🔐 Password Reset Request");

            String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;

            String htmlContent = """
                <html>
                    <body style="font-family: Arial, sans-serif; line-height:1.6; color:#333;">
                        <h2 style="color:#2E86C1;">Password Reset Request</h2>
                        <p>We received a request to reset your password. Click the button below to reset it:</p>
                        <p style="text-align:center;">
                            <a href="%s" 
                               style="background-color:#2E86C1; color:white; padding:10px 20px; text-decoration:none; border-radius:5px;">
                               Reset Password
                            </a>
                        </p>
                        <p>If you did not request a password reset, please ignore this email.</p>
                        <hr>
                        <p style="font-size:12px; color:#888;">This is an automated message. Do not reply.</p>
                    </body>
                </html>
            """.formatted(resetUrl);

            helper.setText(htmlContent, true); // "true" enables HTML

            javaMailSender.send(message);

        } catch (MailException | MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
