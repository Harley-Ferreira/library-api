package com.harley.library.implementations;

import com.harley.library.entities.Book;
import com.harley.library.exceptions.BusinessException;
import com.harley.library.respositories.BookRepository;
import com.harley.library.services.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImp implements BookService {

    private BookRepository bookRepository;

    public BookServiceImp(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn already registered");
        }
        return bookRepository.save(book);
    }
}
