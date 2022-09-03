package com.harley.library.controllers;

// TDD
//Using JUnit 5

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harley.library.dtos.BookDTO;
import com.harley.library.entities.Book;
import com.harley.library.exceptions.BusinessException;
import com.harley.library.services.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Must successfully create a book.")
    void createBookTest() throws Exception {
        // Scenary
        BookDTO bookDTO = getCreateNewBookDTO();
        Book book = Book.builder().id(1l).title("My Adventures").author("Mary").isbn("1234").build();

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(book);
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        // Execution
        MockHttpServletRequestBuilder requestBuilders = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // Verification
        mockMvc.perform(requestBuilders)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDTO.getIsbn()));
    }

    @Test
    @DisplayName("Should throw a validation error when there isn't data enough to create a book.")
    void createInvalidBookTest() throws Exception {
        // Scenary
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        // Execution
        MockHttpServletRequestBuilder requestBuilders = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // Verification
        mockMvc.perform(requestBuilders)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));

    }

    @Test
    @DisplayName("Should throw an error when trying to register a book with an existing isbn.")
    void createBookWithDuplicatedIsnb() throws Exception{
        // Scenary
        BookDTO bookDTO = getCreateNewBookDTO();
        String json = new ObjectMapper().writeValueAsString(bookDTO);
        String errorMessage = "isbn already registered.";
        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(errorMessage));

        // Execution
        MockHttpServletRequestBuilder requestBuilders = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // Verification
        mockMvc.perform(requestBuilders)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(errorMessage));
    }

    @Test
    @DisplayName("Must get the information from a saved book")
    void getBookDetails() throws Exception {
        // Given
        Long id =  1l;
        Book book = getCreateNewBook();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));

        // When
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // Then
        BookDTO bookDTO = getCreateNewBookDTO();
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDTO.getIsbn()));
    }

    @Test
    @DisplayName("Should return an exception when not finding a book with the id passed.")
    void bookNotFound() throws Exception {

        // Given
        BDDMockito.given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        // When
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 3))
                .accept(MediaType.APPLICATION_JSON);

        // Then
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("Must successfully delete a book")
    void deleteBook() throws Exception {
        // Given
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));

        // When
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        // Then
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("Should give an error when trying to delete a book that doesn't exist.")
    void errorWhenDeletingBook() throws Exception {
        // Given
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // When
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        // Then
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("Must successfully update a book")
    void updateBookTest() throws Exception {
        // Given
        Long id = 1l;
        Book oldBook = Book.builder().id(1l).title("We are together").author("Kaik").isbn("5554").build();
        BDDMockito.given(bookService.getById(id))
                .willReturn(Optional.of(oldBook));

        Book newBook = getCreateNewBook();
        String json = new ObjectMapper().writeValueAsString(newBook);
        BDDMockito.given(bookService.update(Mockito.any()))
                .willReturn(newBook);

        // When
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // Then
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(newBook.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(newBook.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(newBook.getIsbn()));
    }

    @Test
    @DisplayName("Should give an error 404 when trying to update a book that doesn't exist.")
    void errorWhenUpdateABook() throws Exception {
        // Given
        String json = new ObjectMapper().writeValueAsString(getCreateNewBook());
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // When
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Then
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("Should filter books")
    void findBooksTest() throws Exception {
        // Scenary
        Book book = getCreateNewBook();

        BDDMockito.given(bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        // Execution
        String url = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(url))
                .accept(MediaType.APPLICATION_JSON);

        // Verification
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }

    private BookDTO getCreateNewBookDTO() {
        return BookDTO.builder().title("My Adventures").author("Mary").isbn("1234").build();
    }

    private Book getCreateNewBook() {
        return Book.builder().id(1l).title("My Adventures").author("Mary").isbn("1234").build();
    }
}
