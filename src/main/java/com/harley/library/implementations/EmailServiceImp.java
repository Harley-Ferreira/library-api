package com.harley.library.implementations;

import com.harley.library.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("application.email.dafult.sender")
    private String sender;

    @Override
    public void sendEmails(List<String> emailsList, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setSubject("My Library");
        mailMessage.setText(message);
        String[] emails = emailsList.toArray(new String[emailsList.size()]);
        mailMessage.setTo(emails);

        javaMailSender.send(mailMessage);
    }
}
