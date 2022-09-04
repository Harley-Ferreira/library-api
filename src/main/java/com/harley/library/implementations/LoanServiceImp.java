package com.harley.library.implementations;

import com.harley.library.entities.Loan;
import com.harley.library.exceptions.BusinessException;
import com.harley.library.respositories.LoanRepository;
import com.harley.library.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanServiceImp implements LoanService {

    private final LoanRepository loanRepository;

    @Override
    public Loan save(Loan loan) {
        if (loanRepository.existsByBookAndNotReturned(loan.getBook()))
            throw new BusinessException("Book already borrowed");

        return loanRepository.save(loan);
    }
}
