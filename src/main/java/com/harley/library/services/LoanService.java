package com.harley.library.services;

import com.harley.library.dtos.LoanDTO;
import com.harley.library.entities.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanDTO loanDTO, Pageable pageable);
}
