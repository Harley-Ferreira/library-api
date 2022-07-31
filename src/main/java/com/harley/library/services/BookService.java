package com.harley.library.services;

import com.harley.library.entities.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Long id);
}
