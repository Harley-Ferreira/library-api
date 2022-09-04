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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

}
