package com.example.finance_control;

import com.example.finance_control.repositories.categories.Category;
import com.example.finance_control.repositories.transactions.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;
@Component
public class MainPage extends JFrame {
    private final Data data;
    private JPanel transactionsPanel;
    private JScrollPane categoriesScrollPane;
    private int currentCategoryId = -1;
    @Autowired
    public MainPage(Data data) {
        this.data = data;
        initWindow();
    }

    private void initWindow() {
        setTitle("Finance Control");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        categoriesScrollPane = createCategoriesScrollPane();

        transactionsPanel = new JPanel();
        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));
        transactionsPanel.add(new JLabel("Select a category to view transactions"));
        JScrollPane transactionsScrollPane = new JScrollPane(transactionsPanel);
        transactionsScrollPane.setPreferredSize(new Dimension(600, 600));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton[] buttons = {
                new JButton("Add Category"), new JButton("Add Transaction"),
                new JButton("Delete Category"), new JButton("Delete Transaction"),
                new JButton("Reload")
        };
        for (var btn : buttons) buttonPanel.add(btn);
        buttons[0].addActionListener(e -> showAddCategoryDialog());
        buttons[1].addActionListener(e -> showAddTransactionDialog());
        buttons[2].addActionListener(e -> showDeleteCategoryDialog());
        buttons[3].addActionListener(e -> showDeleteTransactionDialog());
        buttons[4].addActionListener(e -> reloadData());

        add(categoriesScrollPane, BorderLayout.WEST);
        add(transactionsScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private JScrollPane createCategoriesScrollPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (Category category : data.getCategories()) {
            JButton btn = new JButton(category.getTitle());
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            int id = category.getId();
            btn.addActionListener(e -> {
                currentCategoryId = id;
                showTransactions(id);
            });
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(200, 600));
        return scrollPane;
    }

    private void showTransactions(int categoryId) {
        SwingUtilities.invokeLater(() -> {
            transactionsPanel.removeAll();
            List<Transaction> transactions = data.getTransactionByCategoryId(categoryId);
            if (transactions.isEmpty()) {
                transactionsPanel.add(new JLabel("No transactions for this category"));
            } else {
                for (Transaction t : transactions) {
                    JLabel label = new JLabel(
                            "description: %s | price: %d | id: %d"
                                    .formatted(t.getDescription(), t.getPrice(), t.getId())
                    );
                    label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    transactionsPanel.add(label);
                    transactionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
            transactionsPanel.revalidate();
            transactionsPanel.repaint();
        });
    }

    private void showAddCategoryDialog() {
        JDialog dialog = new JDialog(this, "Add Category", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new GridLayout(3, 1, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField titleField = new JTextField();
        JButton submit = new JButton("Add");
        submit.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                showError(dialog, "Title cannot be empty");
                return;
            }
            try {
                data.addCategory(title);
                refreshCategories();
                JOptionPane.showMessageDialog(dialog, "Category added successfully");
                dialog.dispose();
            } catch (Exception ex) {
                showError(dialog, "Error adding category: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel("Category Title:"));
        dialog.add(titleField);
        dialog.add(submit);
        dialog.setVisible(true);
    }

    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog(this, "Add Transaction", true);
        dialog.setSize(300, 250);
        dialog.setLayout(new GridLayout(7, 1, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField descField = new JTextField(), priceField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>();
        List<Category> categories = data.getCategories();
        if (categories.isEmpty()) {
            showError(dialog, "No categories available. Please add a category first.");
            dialog.dispose();
            return;
        }
        categories.forEach(c -> categoryCombo.addItem(c.getTitle() + " (ID: " + c.getId() + ")"));

        JButton submit = new JButton("Add");
        submit.addActionListener(e -> {
            String desc = descField.getText().trim(), priceText = priceField.getText().trim();
            int catIndex = categoryCombo.getSelectedIndex();
            if (desc.isEmpty() || priceText.isEmpty() || catIndex == -1) {
                showError(dialog, "All fields must be filled");
                return;
            }
            try {
                long price = Long.parseLong(priceText);
                int catId = categories.get(catIndex).getId();
                data.addTransaction(desc, price, catId);
                if (currentCategoryId == catId) showTransactions(catId);
                JOptionPane.showMessageDialog(dialog, "Transaction added successfully");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                showError(dialog, "Price must be a valid number");
            } catch (Exception ex) {
                showError(dialog, "Error adding transaction: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel("Description:"));
        dialog.add(descField);
        dialog.add(new JLabel("Price:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Category:"));
        dialog.add(categoryCombo);
        dialog.add(submit);
        dialog.setVisible(true);
    }

    private void showDeleteCategoryDialog() {
        JDialog dialog = new JDialog(this, "Delete Category", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new GridLayout(3, 1, 10, 10));
        dialog.setLocationRelativeTo(this);

        JComboBox<String> categoryCombo = new JComboBox<>();
        List<Category> categories = data.getCategories();
        if (categories.isEmpty()) {
            showError(dialog, "No categories available to delete.");
            dialog.dispose();
            return;
        }
        categories.forEach(c -> categoryCombo.addItem(c.getTitle() + " (ID: " + c.getId() + ")"));

        JButton submit = new JButton("Delete");
        submit.addActionListener(e -> {
            int catIndex = categoryCombo.getSelectedIndex();
            if (catIndex == -1) {
                showError(dialog, "Please select a category");
                return;
            }
            try {
                int catId = categories.get(catIndex).getId();
                data.deleteCategory(catId);
                if (currentCategoryId == catId) {
                    currentCategoryId = -1;
                    transactionsPanel.removeAll();
                    transactionsPanel.add(new JLabel("Select a category to view transactions"));
                    transactionsPanel.revalidate();
                    transactionsPanel.repaint();
                }
                refreshCategories();
                JOptionPane.showMessageDialog(dialog, "Category deleted successfully");
                dialog.dispose();
            } catch (Exception ex) {
                showError(dialog, "Error deleting category: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel("Select Category:"));
        dialog.add(categoryCombo);
        dialog.add(submit);
        dialog.setVisible(true);
    }

    private void showDeleteTransactionDialog() {
        JDialog dialog = new JDialog(this, "Delete Transaction", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new GridLayout(3, 1, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField idField = new JTextField();
        JButton submit = new JButton("Delete");
        submit.addActionListener(e -> {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                showError(dialog, "Transaction ID cannot be empty");
                return;
            }
            try {
                data.deleteTransaction(Integer.parseInt(idText));
                if (currentCategoryId != -1) showTransactions(currentCategoryId);
                JOptionPane.showMessageDialog(dialog, "Transaction deleted successfully");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                showError(dialog, "Transaction ID must be a valid number");
            } catch (Exception ex) {
                showError(dialog, "Error deleting transaction: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel("Transaction ID:"));
        dialog.add(idField);
        dialog.add(submit);
        dialog.setVisible(true);
    }

    private void refreshCategories() {
        SwingUtilities.invokeLater(() -> {
            getContentPane().remove(categoriesScrollPane);
            categoriesScrollPane = createCategoriesScrollPane();
            getContentPane().add(categoriesScrollPane, BorderLayout.WEST);
            revalidate();
            repaint();
        });
    }

    private void reloadData() {
        SwingUtilities.invokeLater(() -> {
            refreshCategories();
            transactionsPanel.removeAll();
            if (currentCategoryId != -1) {
                showTransactions(currentCategoryId);
            } else {
                transactionsPanel.add(new JLabel("Select a category to view transactions"));
            }
            transactionsPanel.revalidate();
            transactionsPanel.repaint();
        });
    }

    private void showError(JDialog dialog, String message) {
        JOptionPane.showMessageDialog(dialog, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}