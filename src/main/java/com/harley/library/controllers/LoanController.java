package com.harley.library.controllers;

import com.harley.library.dtos.LoanDTO;
import com.harley.library.entities.Book;
import com.harley.library.entities.Loan;
import com.harley.library.services.BookService;
import com.harley.library.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO) {
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan loan = Loan.builder()
                .book(book)
                .customer(loanDTO.getCustomer())
                .date(LocalDate.now())
                .build();

        loan = loanService.save(loan);

        return loan.getId();
    }


}
