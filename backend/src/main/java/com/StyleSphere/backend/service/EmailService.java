package com.StyleSphere.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService
{

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine; // Injected Thymeleaf engine

    public void sendHtmlEmail(String to, String subject, String otpCode) throws MessagingException {
        // 1. Prepare data for the template
        Context context = new Context();
        context.setVariable("otpCode", otpCode);

        // 2. Process the HTML template
        String htmlContent = templateEngine.process("otp-email", context);

        // 3. Send as MimeMessage (HTML)
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // True tells it this is HTML
        helper.setFrom("your-email@gmail.com");

        mailSender.send(message);
    }


}
