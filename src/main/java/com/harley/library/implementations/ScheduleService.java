package com.harley.library.implementations;

import com.harley.library.entities.Loan;
import com.harley.library.services.EmailService;
import com.harley.library.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Value("${application.email.lateloan.message}")
    private String message;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMail() {
        List<Loan> allLateLoans = loanService.getAllLateLoans();
        List<String> emailsList = allLateLoans.stream().map(Loan::getCustomerEmail).toList();


        emailService.sendEmails(emailsList, message);
    }
}
