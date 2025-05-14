package com.app.library;

import com.app.library.books.Book;
import com.app.library.books.BookService;
import com.app.library.books.BooksRepository;
import com.app.library.books.FileImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class Page extends JFrame {
    private final BookService bookService;
    private final FileImport fileImport;
    private JList<String> bookList;
    private DefaultListModel<String> bookListModel;
    private BooksRepository booksRepository;

    @Autowired
    public Page(BookService bookService, FileImport fileImport, BooksRepository booksRepository) {
        this.bookService = bookService;
        this.fileImport = fileImport;
        this.booksRepository = booksRepository;
        initializeFirstWindow();
    }

    private void initializeFirstWindow() {
        setTitle("Library Book Selection");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Book list
        bookListModel = new DefaultListModel<>();
        refreshBookList();
        bookList = new JList<>(bookListModel);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                openBookWindow();
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookList);
        add(scrollPane, BorderLayout.CENTER);

        // Add button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> openFilePage());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshBookList() {
        bookListModel.clear();
        try {
            booksRepository.findAll().forEach(book ->
                    bookListModel.addElement(book.getTitle() + " (ID: " + book.getId() + ")")
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFilePage() {
        SwingUtilities.invokeLater(() -> {
            FilePage filePage = new FilePage(fileImport);
            filePage.setVisible(true);
        });
    }

    private void openBookWindow() {
        String selected = bookList.getSelectedValue();
        if (selected == null) return;

        // Extract ID (assumes format "Title (ID: X)")
        try {
            int id = Integer.parseInt(selected.substring(selected.lastIndexOf(": ") + 2, selected.length() - 1));
            Book book = bookService.get(id);
            showBookWindow(book);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBookWindow(Book book) {
        JFrame bookFrame = new JFrame("Book: " + book.getTitle());
        bookFrame.setSize(500, 400);
        bookFrame.setLocationRelativeTo(null);
        bookFrame.setLayout(new BorderLayout());

        // Book content
        JTextArea bookTextArea = new JTextArea(book.getBookText());
        bookTextArea.setEditable(false);
        bookTextArea.setLineWrap(true);
        bookTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(bookTextArea);
        bookFrame.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton("Delete");
        JButton editButton = new JButton("Edit");
        JButton exitButton = new JButton("Exit");

        deleteButton.addActionListener(e -> {
            try {
                bookService.delete(book.getId());
                bookFrame.dispose();
                JOptionPane.showMessageDialog(this, "Book deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshBookList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editButton.addActionListener(e -> {
            // Open edit dialog
            JTextField titleField = new JTextField(book.getTitle());
            JTextField authorField = new JTextField(book.getAuthor());
            JTextArea textArea = new JTextArea(book.getBookText(), 10, 30);
            JScrollPane textScroll = new JScrollPane(textArea);

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Title:"));
            panel.add(titleField);
            panel.add(new JLabel("Author:"));
            panel.add(authorField);
            panel.add(new JLabel("Text:"));
            panel.add(textScroll);

            int result = JOptionPane.showConfirmDialog(bookFrame, panel, "Edit Book", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    bookService.update(book.getId(), titleField.getText(), authorField.getText(), textArea.getText());
                    JOptionPane.showMessageDialog(bookFrame, "Book updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    bookFrame.dispose();
                    refreshBookList();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(bookFrame, "Error updating book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        exitButton.addActionListener(e -> bookFrame.dispose());

        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(exitButton);
        bookFrame.add(buttonPanel, BorderLayout.NORTH);

        SwingUtilities.invokeLater(() -> bookFrame.setVisible(true));
    }
}