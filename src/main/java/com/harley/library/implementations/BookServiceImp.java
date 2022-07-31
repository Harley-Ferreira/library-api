package com.harley.library.implementations;

import com.harley.library.entities.Book;
import com.harley.library.exceptions.BusinessException;
import com.harley.library.respositories.BookRepository;
import com.harley.library.services.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public Optional<Book> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Book update(Book book) {
        return bookRepository.save(book);
    }


}
