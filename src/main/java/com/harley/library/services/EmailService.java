package com.harley.library.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmailService {
    void sendEmails(List<String> emailsList, String message);
}
