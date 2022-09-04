package com.harley.library.service;

import com.harley.library.entities.Book;
import com.harley.library.entities.Loan;
import com.harley.library.exceptions.BusinessException;
import com.harley.library.implementations.LoanServiceImp;
import com.harley.library.respositories.LoanRepository;
import com.harley.library.services.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    private LoanRepository loanRepository;
    private LoanService loanService;

    @BeforeEach
    private void setUp(){
        this.loanService = new LoanServiceImp(loanRepository);
    }

    @Test
    @DisplayName("Must save a loan successfully")
    void givenALoan_whenCallSave_ThenReturnSavedLoan() {
        Loan loan = createLoan();
        Loan savedLoan = createLoan();

        when(loanRepository.save(loan)).thenReturn(savedLoan);
        when(loanRepository.existsByBookAndNotReturned(loan.getBook())).thenReturn(false);

        Loan returnedLoan = loanService.save(loan);

        assertThat(savedLoan.getId()).isEqualTo(returnedLoan.getId());
        assertThat(savedLoan.getBook()).isEqualTo(returnedLoan.getBook());
        assertThat(savedLoan.getCustomer()).isEqualTo(returnedLoan.getCustomer());
        assertThat(savedLoan.getDate()).isEqualTo(returnedLoan.getDate());
    }

    @Test
    @DisplayName("Must throw an exception when call save loan")
    void givenAnBorrowedBook_whenCallSave_ThenThrowANException() {
        Loan loan = createLoan();
        Loan savedLoan = createLoan();

        when(loanRepository.save(loan)).thenReturn(savedLoan);
        when(loanRepository.existsByBookAndNotReturned(loan.getBook())).thenReturn(true);

        Throwable throwable = catchThrowable(() -> loanService.save(loan));

        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already borrowed");
        verify(loanRepository, never()).save(loan);
    }

    public static Loan createLoan() {
        return Loan.builder()
                .id(1L)
                .customer("Harley")
                .date(LocalDate.now())
                .book(Book.builder().id(1L).build())
                .build();
    }
}
