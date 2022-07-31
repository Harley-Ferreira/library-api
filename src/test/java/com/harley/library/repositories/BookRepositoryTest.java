package com.harley.library.repositories;


import com.harley.library.entities.Book;
import com.harley.library.respositories.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Must return true when there is no book in the base with the informed isbn.")
    void mustReturnTrueWhenThereIsAlreadyBookWithIsbn() {
        // Scenary
        Book book = createValidBook();
        testEntityManager.persist(book);

        // Execution
        String isbn = "1234";
        boolean exist = bookRepository.existsByIsbn(isbn);

        // Verification
        Assertions.assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("Must return false when there is no book in the base with the informed isbn.")
    void returnFalseWhenIsbnDoesntExist() {
        // Scenary

        // Execution
        String isbn = "1234";
        boolean exist = bookRepository.existsByIsbn(isbn);

        // Verification
        Assertions.assertThat(exist).isFalse();

    }

    private Book createValidBook() {
        return Book.builder().title("My Adventures").author("Mary").isbn("1234").build();
    }


}
