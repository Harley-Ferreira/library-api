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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Must successfully create a book.")
    public void createBookTest() throws Exception {
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
    public void createInvalidBookTest() throws Exception {
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
    public void createBookWithDuplicatedIsnb() throws Exception{
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


    private BookDTO getCreateNewBookDTO() {
        return BookDTO.builder().title("My Adventures").author("Mary").isbn("1234").build();
    }
}
