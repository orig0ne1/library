package com.example.finance_control;

import com.example.finance_control.repositories.categories.Category;
import com.example.finance_control.repositories.transactions.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Component
public class Data {
    private final Connection connection;

    @Autowired
    public Data(Connection connection) {
        this.connection = connection;
    }

    public List<Transaction> getTransactionByCategoryId(Integer categoryId) {
        String query = "SELECT * FROM transactions WHERE category_id = ?;";
        List<Transaction> transactionList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoryId);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(set.getInt("id"));
                    transaction.setDescription(set.getString("description"));
                    transaction.setPrice(set.getLong("price")); // Long, так как в классе Long
                    transaction.setCategoryId(set.getInt("category_id"));
                    transactionList.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return transactionList;
    }

    public List<Category> getCategories() {
        String query = "SELECT * FROM categories;";
        List<Category> categories = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet set = statement.executeQuery(query)) {
            while (set.next()) {
                Category category = new Category();
                category.setId(set.getInt("id"));
                category.setTitle(set.getString("title"));
                categories.add(category);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return categories;
    }

    public void addCategory(String title) {
        String query = "INSERT INTO categories (title) VALUES (?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addTransaction(String description, Long price, Integer categoryId) {
        String query = "INSERT INTO transactions (description, price, category_id) VALUES (?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, description);
            statement.setLong(2, price);
            statement.setInt(3, categoryId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteCategory(int categoryId) {
        String sql = "DELETE FROM categories WHERE id = ?; DELETE FROM transactions WHERE category_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoryId);
            statement.setInt(2, categoryId);
            statement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}