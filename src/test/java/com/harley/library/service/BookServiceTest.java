package com.harley.library.service;

import com.harley.library.entities.Book;
import com.harley.library.exceptions.BusinessException;
import com.harley.library.implementations.BookServiceImp;
import com.harley.library.respositories.BookRepository;
import com.harley.library.services.BookService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

    BookService bookService;
    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImp(bookRepository);
    }

    @Test
    @DisplayName("Must save a book")
    void saveBookTest() {
        //Scenary
        Book book = createValidBook();
        Book book2 = createValidBook();
        book2.setId(1l);
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(bookService.save(book)).thenReturn(book2);

        // Execution
        Book savedBook = bookService.save(book);

        // Verification
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("1234");
        assertThat(savedBook.getAuthor()).isEqualTo("Mary");
        assertThat(savedBook.getTitle()).isEqualTo("My Adventures");
    }

    @Test
    @DisplayName("Should throw an error when trying to register a book with an existing isbn.")
    void shouldNotSaveABookWithDuplicatedIsbn() {
        // Scenary
        Book book =  createValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        // Execution
        Throwable throwble = Assertions.catchThrowable(() -> bookService.save(book));

        // Verification
        assertThat(throwble)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn already registered");
        Mockito.verify(bookRepository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("should get a book by id successfully.")
    void getBookById() {
        // Scenary
        Long id = 1l;
        Book book = createValidBook();
        book.setId(1l);
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // Execution
        Optional<Book> foundBook = bookService.getById(id);

        // Verification
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());

    }

    @Test
    @DisplayName("Should return empty when trying to get a book.")
    void getEmptyBook() {
        // Scenary
        Long id = 1l;
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // Execution
        Optional<Book> foundBook = bookService.getById(id);

        // Verification
        Assertions.assertThat(foundBook.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should successfully delete a book by the id passed.")
    void deleteABook() {
        // Scenary
        Book book = createValidBook();
        book.setId(1l);

        // Execution
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.delete(book));

        // Verification
        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Should give an error when trying to delete a book.")
    void errorDeleteABook() {
        // Scenary
        Book book = new Book();

        // Execution
        Throwable throwable = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));

        // Verification
        Mockito.verify(bookRepository, Mockito.never()).delete(book);
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id can't be null");
    }

    @Test
    @DisplayName("Should successfully update a book by the id passed.")
    void updateABook() {
        // Scenary
        Book book = createValidBook();
        book.setId(1l);
        Mockito.when(bookRepository.save(book)).thenReturn(book);

        // Execution
        Book bookUpdated = org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.update(book));

        // Verification
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
        Assertions.assertThat(book.getId()).isEqualTo(bookUpdated.getId());
        Assertions.assertThat(book.getTitle()).isEqualTo(bookUpdated.getTitle());
        Assertions.assertThat(book.getIsbn()).isEqualTo(bookUpdated.getIsbn());
        Assertions.assertThat(book.getAuthor()).isEqualTo(bookUpdated.getAuthor());
    }

    @Test
    @DisplayName("Should give an error when trying to update a book.")
    void errorUpdateABook() {
        // Scenary
        Book book = new Book();

        // Execution
        Throwable throwable = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

        // Verification
        Mockito.verify(bookRepository, Mockito.never()).save(book);
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id can't be null");
    }

    private Book createValidBook() {
        return Book.builder().title("My Adventures").author("Mary").isbn("1234").build();
    }
}
