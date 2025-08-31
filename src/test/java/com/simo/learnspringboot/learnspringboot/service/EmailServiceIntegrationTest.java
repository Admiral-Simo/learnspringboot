package com.simo.learnspringboot.learnspringboot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendRealResetPasswordEmail() {
        String recipient = "mohamedkhalisgm@gmail.com"; // replace with an email you can check
        String token = "test-token-123";

        emailService.sendResetPasswordEmail(recipient, token);

        System.out.println("Email sent! Check inbox for " + recipient);
    }
}
