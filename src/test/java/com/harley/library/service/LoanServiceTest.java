package com.harley.library.service;

import com.harley.library.dtos.LoanDTO;
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
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Should get informations of loan by id")
    void giveAnId_WhenCallGetById_ThenReturnALoan() {
        Long id = 1l;
        Loan loan = createLoan();

        Mockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> retunedLoan = loanService.getById(id);

        assertThat(retunedLoan.isPresent()).isTrue();
        assertThat(retunedLoan.get().getId()).isEqualTo(id);
        assertThat(retunedLoan.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(retunedLoan.get().getBook()).isEqualTo(loan.getBook());
        assertThat(retunedLoan.get().getDate()).isEqualTo(loan.getDate());

        verify(loanRepository).findById(id);
    }

    @Test
    @DisplayName("Should update the field returned when passing a loan")
    void givenALoan_WhenCallUpdate_ThenReturnedUpdatedLoan() {
        Loan loan = createLoan();
        loan.setReturned(true);

        when(loanRepository.save(any())).thenReturn(loan);

        Loan returnedLoan = loanService.update(loan);

        assertThat(returnedLoan.getReturned()).isTrue();
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("Should find an object Loan by isbn or customer")
    void givenALoan_WhenCallFind_ThenReturnAListLoan() {
        // Scenary
        LoanDTO loanDTO = LoanDTO.builder().customer("Harley").isbn("123").build();
        Loan loan = createLoan();
        PageRequest pageRequest = PageRequest.of(0,10);
        List<Loan> loanList = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<>(loanList, pageRequest, loanList.size());
        when(loanRepository.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(page);

        // Execution
        Page<Loan> result = loanService.find(loanDTO,pageRequest);

        // Verification
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(loanList);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

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
