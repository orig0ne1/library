package com.app.library.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileImport {
    private final BookService bookService;

    @Autowired
    public FileImport(BookService bookService) {
        this.bookService = bookService;
    }

    public void importFile(Path filePath, String title, String author) throws IOException {
        String bookText = Files.readString(filePath);
        bookService.insert(title, author, bookText);
    }
}