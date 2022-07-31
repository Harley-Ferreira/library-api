package com.harley.library.repositories;


import com.harley.library.entities.Book;
import com.harley.library.respositories.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

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
    @Test
    @DisplayName("Must find a book by id")
    public void findBookById() {
        // Scenary
        Book book = createValidBook();
        testEntityManager.persist(book);

        // Execution
        Optional<Book> foundBook = bookRepository.findById(book.getId());

        // Verification
        Assertions.assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Must save a book")
    public void saveBook() {
        // Scenary
        Book book = createValidBook();

        // Executation
        Book saveBook = bookRepository.save(book);

        // Verification
        Assertions.assertThat(saveBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Must delete a book")
    public void deleteBook() {
        // Scenary
        Book book = createValidBook();
        testEntityManager.persist(book);

        // Execution
        Book foundBook = testEntityManager.find(Book.class, book.getId());
        bookRepository.delete(foundBook);

        // Verification
        Book deleteBook = testEntityManager.find(Book.class, book.getId());
        Assertions.assertThat(deleteBook).isNull();
    }


    private Book createValidBook() {
        return Book.builder().title("My Adventures").author("Mary").isbn("1234").build();
    }


}
