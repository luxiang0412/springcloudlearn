package com.luxiang.swagger3.springdoc.repository;

import com.luxiang.swagger3.springdoc.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author luxiang
 * description  //TODO
 * create       2020-07-01 16:33
 */
@Repository
public class BookRepository {

    private Map<Long, Book> books = new HashMap<>();

    public Optional<Book> findById(long id) {
        return Optional.ofNullable(books.get(id));
    }

    public void add(Book book) {
        books.put(book.getId(), book);
    }

    public Collection<Book> getBooks() {
        return books.values();
    }

    public Page<Book> getBooks(Pageable pageable) {
        int toSkip = pageable.getPageSize() * pageable.getPageNumber();
        List<Book> result = books.values().stream().skip(toSkip).limit(pageable.getPageSize()).collect(toList());

        return new PageImpl<>(result, pageable, books.size());
    }
}
