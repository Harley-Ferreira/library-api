package com.harley.library.repositories;

import com.harley.library.entities.Loan;
import com.harley.library.respositories.LoanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.harley.library.service.LoanServiceTest.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {
    @Autowired
    LoanRepository loanRepository;
    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Should check if there is a loan for an unreturned book")
    void givenAnUnreturnedBook_WhenCallSaveLoan_ThenReturnTrue() {
        Loan loan = createLoan();
        loan.setId(null);
        loan.getBook().setId(null);
        entityManager.persist(loan.getBook());
        entityManager.persist(loan);

        boolean exists = loanRepository.existsByBookAndNotReturned(loan.getBook());

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return a list of loan.")
    void givenALoanDTO_whenCallFindByBookIsbnOrCustomer_ThenReturnListLoan() {
        Loan loan = createLoan();
        loan.setId(null);
        loan.getBook().setId(null);
        entityManager.persist(loan.getBook());
        entityManager.persist(loan);

        Page<Loan> result = loanRepository.findByBookIsbnOrCustomer("123", "Harley", PageRequest.of(0, 10));

        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getContent()).contains(loan);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(result.getPageable().getPageNumber()).isZero();
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return a loan list")
    void givenADeadline_WhenCallGetLateLoan_ThenReturnAListLoan() {
        Loan loan = createLoan();
        loan.setDate(LocalDate.now().minusDays(5));
        loan.setId(null);
        loan.getBook().setId(null);
        entityManager.persist(loan.getBook());
        entityManager.persist(loan);

        List<Loan> result = loanRepository.findAllLateLoans(LocalDate.now().minusDays(4));

        Assertions.assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Should return a empty loan list when there aren't any late loan")
    void givenADeadline_WhenCallGetLateLoan_ThenReturnAnEmptyListLoan() {
        Loan loan = createLoan();
        loan.setDate(LocalDate.now());
        loan.setId(null);
        loan.getBook().setId(null);
        entityManager.persist(loan.getBook());
        entityManager.persist(loan);

        List<Loan> result = loanRepository.findAllLateLoans(LocalDate.now().minusDays(4));

        Assertions.assertThat(result).isEmpty();
    }
}
