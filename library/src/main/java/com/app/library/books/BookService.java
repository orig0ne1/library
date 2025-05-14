package com.app.library.books;

import com.app.library.DataClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class BookService {
    private final Connection connection;
    private final BooksRepository booksRepository;

    @Autowired
    public BookService(DataClass dataClass, BooksRepository booksRepository) {
        connection = dataClass.getConnection();
        this.booksRepository = booksRepository;
    }

    public void insert(String title, String author, String bookText) {
        String sql = """
                INSERT INTO books(title, author, book_text) VALUES(?,?,?);
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, bookText);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Book get(int id) {
        return booksRepository.findById(id).get();

    }

    public void update(int id, String title, String author, String bookText) {
        String sql = """
               UPDATE books
               SET title = ?,
                   author = ?,
                   book_text = ?
               WHERE id = ?;
               """;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, bookText);
            statement.setInt(4, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = """
                DELETE FROM books
                WHERE id = ?;
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
