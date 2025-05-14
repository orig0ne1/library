package com.app.library;

import com.app.library.books.FileImport;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FilePage extends JFrame {
    private final FileImport fileImport;
    private JTextField titleField;
    private JTextField authorField;
    private JLabel selectedFileLabel;

    public FilePage(FileImport fileImport) {
        this.fileImport = fileImport;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Import Book from File");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Form panel for input fields
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));



        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        formPanel.add(authorField);

        formPanel.add(new JLabel("Selected File:"));
        selectedFileLabel = new JLabel("No file selected");
        formPanel.add(selectedFileLabel);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton selectButton = new JButton("Select File");
        JButton importButton = new JButton("Import Book");

        selectButton.addActionListener(e -> selectFile());
        importButton.addActionListener(e -> importFile());

        buttonPanel.add(selectButton);
        buttonPanel.add(importButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void selectFile() {
        SwingUtilities.invokeLater(() -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedFileLabel.setText(selectedFile.getAbsolutePath());
            }
        });
    }

    private void importFile() {
        SwingUtilities.invokeLater(() -> {
            String filePath = selectedFileLabel.getText();
            if ("No file selected".equals(filePath)) {
                JOptionPane.showMessageDialog(this, "Please select a file.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                fileImport.importFile(new File(filePath).toPath(), title, author);

                JOptionPane.showMessageDialog(this, "Book imported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error importing file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}