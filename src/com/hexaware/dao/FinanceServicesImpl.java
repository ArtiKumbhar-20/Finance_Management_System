package com.hexaware.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;

import com.hexaware.dto.Expense;
import com.hexaware.dto.ExpenseCategory;
import com.hexaware.dto.User;
import com.hexaware.myexceptions.ExpenseNotFoundException;
import com.hexaware.myexceptions.InvalidFieldException;
import com.hexaware.myexceptions.UserNotFoundException;

public class FinanceServicesImpl implements IFinanceServices {

    private Connection myConnectionObject;

    public FinanceServicesImpl(Connection myConnectionObject) {
        super();
        this.myConnectionObject = myConnectionObject;
    }

    @Override
    public boolean createUser(User user) throws SQLException, InvalidFieldException {
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty() ||
            user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new InvalidFieldException("Username, password, and email must not be null or empty");
        }

        boolean addStatus = false;
        PreparedStatement myStatement = myConnectionObject.prepareStatement("INSERT INTO USERS (username, password, email) VALUES (?, ?, ?)");
        myStatement.setString(1, user.getUsername());
        myStatement.setString(2, user.getPassword());
        myStatement.setString(3, user.getEmail());
        addStatus = !myStatement.execute();
        myStatement.close();
        return addStatus;
    }

    
    @Override
    public boolean createExpense(Expense expense) throws SQLException, InvalidFieldException {
        if (expense.getUserId() <= 0 || expense.getAmount() <= 0 || expense.getCategoryId() <= 0 ||
            expense.getDate() == null || expense.getDescription() == null || expense.getDescription().isEmpty()) {
            throw new InvalidFieldException("All fields must be valid and non-empty");
        }

        boolean addStatus = false;
        PreparedStatement myStatement = myConnectionObject
                .prepareStatement("INSERT INTO Expenses (user_id, amount, category_id, date, description) VALUES (?, ?, ?, ?, ?)");
        myStatement.setInt(1, expense.getUserId());
        myStatement.setDouble(2, expense.getAmount());
        myStatement.setInt(3, expense.getCategoryId());
        myStatement.setDate(4, new java.sql.Date(expense.getDate().getTime()));
        myStatement.setString(5, expense.getDescription());
        addStatus = !myStatement.execute();
        myStatement.close();
        return addStatus;
    }

    
    @Override
    public boolean deleteUser(int userId) throws SQLException, UserNotFoundException {
        PreparedStatement checkStatement = this.myConnectionObject.prepareStatement("SELECT * FROM Users WHERE user_id=?");
        checkStatement.setInt(1, userId);
        ResultSet checkResult = checkStatement.executeQuery();
        if (!checkResult.next()) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
        
        boolean removeStatus = false;
        PreparedStatement myStatement = this.myConnectionObject.prepareStatement("DELETE FROM Users WHERE user_id=?");
        myStatement.setInt(1, userId);
        removeStatus = !myStatement.execute();
        myStatement.close();
        return removeStatus;
    }

    
    @Override
    public boolean deleteExpense(int expenseId) throws SQLException, ExpenseNotFoundException {
        PreparedStatement checkStatement = this.myConnectionObject.prepareStatement("SELECT * FROM Expenses WHERE expense_id=?");
        checkStatement.setInt(1, expenseId);
        ResultSet checkResult = checkStatement.executeQuery();
        if (!checkResult.next()) {
            throw new ExpenseNotFoundException("Expense with ID " + expenseId + " not found.");
        }
        
        boolean removeStatus = false;
        PreparedStatement myStatement = this.myConnectionObject.prepareStatement("DELETE FROM Expenses WHERE expense_id=?");
        myStatement.setInt(1, expenseId);
        removeStatus = !myStatement.execute();
        myStatement.close();
        return removeStatus;
    }
    
    
    @Override
    public boolean updateExpense(int userId, Expense expense) throws SQLException, ExpenseNotFoundException, InvalidFieldException {
        if (userId <= 0 || expense.getExpenseId() <= 0 || expense.getAmount() <= 0 || expense.getCategoryId() <= 0 ||
            expense.getDate() == null || expense.getDescription() == null || expense.getDescription().isEmpty()) {
            throw new InvalidFieldException("All fields must be valid and non-empty");
        }

        findExpense(expense.getExpenseId());

        boolean updateStatus = false;
        PreparedStatement myStatement = this.myConnectionObject.prepareStatement(
                "UPDATE Expenses SET amount=?, category_id=?, date=?, description=? WHERE expense_id=? AND user_id=?");
        myStatement.setDouble(1, expense.getAmount());
        myStatement.setInt(2, expense.getCategoryId());
        myStatement.setDate(3, new java.sql.Date(expense.getDate().getTime()));
        myStatement.setString(4, expense.getDescription());
        myStatement.setInt(5, expense.getExpenseId());
        myStatement.setInt(6, userId);
        updateStatus = !myStatement.execute();
        myStatement.close();
        return updateStatus;
    }

    
    public User findUser(int userId) throws SQLException, UserNotFoundException {
        PreparedStatement findStatement = myConnectionObject.prepareStatement("SELECT * FROM Users WHERE user_id = ?");
        findStatement.setInt(1, userId);
        ResultSet resultSet = findStatement.executeQuery();
        if (resultSet.next()) {
            int foundUserId = resultSet.getInt("user_id");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            String email = resultSet.getString("email");
            return new User(foundUserId, username, password, email);
        } else {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
    }

    
    private Expense findExpense(int expenseId) throws SQLException, ExpenseNotFoundException {
        PreparedStatement findStatement = myConnectionObject.prepareStatement("SELECT * FROM Expenses WHERE expense_id = ?");
        findStatement.setInt(1, expenseId);
        ResultSet resultSet = findStatement.executeQuery();
        if (resultSet.next()) {
            int foundExpenseId = resultSet.getInt("expense_id");
            int userId = resultSet.getInt("user_id");
            double amount = resultSet.getDouble("amount");
            int categoryId = resultSet.getInt("category_id");
            Date date = resultSet.getDate("date");
            String description = resultSet.getString("description");
            return new Expense(foundExpenseId, userId, amount, categoryId, date, description);
        } else {
            throw new ExpenseNotFoundException("Expense with ID " + expenseId + " not found.");
        }
    }

    
    @Override
    public ArrayList<Expense> getAllExpensesByUser(int userId) throws SQLException, ExpenseNotFoundException, InvalidFieldException {
        if (userId <= 0) {
            throw new InvalidFieldException("User ID must be a positive integer");
        }

        PreparedStatement myStatement = this.myConnectionObject.prepareStatement(
                "SELECT e.expense_id, e.amount, e.date, e.description, " +
                "ec.category_name, u.username, u.email " +
                "FROM Expenses e " +
                "JOIN Users u ON e.user_id = u.user_id " +
                "JOIN ExpenseCategories ec ON e.category_id = ec.category_id " +
                "WHERE e.user_id = ?");
        myStatement.setInt(1, userId);
        ResultSet myResult = myStatement.executeQuery();

        ArrayList<Expense> expenseList = new ArrayList<>();
        String username = "";
        String email = "";

        while (myResult.next()) {
            int expenseId = myResult.getInt("expense_id");
            double amount = myResult.getDouble("amount");
            Date date = myResult.getDate("date");
            String description = myResult.getString("description");
            String categoryName = myResult.getString("category_name");
            username = myResult.getString("username");
            email = myResult.getString("email");

            ExpenseCategory category = new ExpenseCategory(0, categoryName);
            User user = new User(userId, username, "", email);  
            Expense expense = new Expense(expenseId, userId, amount, 0, date, description, user, category);
            expenseList.add(expense);
        }

        if (expenseList.isEmpty()) {
            throw new ExpenseNotFoundException("No expenses found for User ID " + userId);
        }

        printExpenses(userId, username, email, expenseList);

        return expenseList;
    }

    
    private void printExpenses(int userId, String username, String email, ArrayList<Expense> expenses) {
        System.out.println("Expenses for User ID: " + userId);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-9s | %-20s | %-15s | %-30s\n",
                "Expense ID", "Amount", "Category Name", "Date", "Description");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        for (Expense expense : expenses) {
            System.out.printf("%-10d | %-9.2f | %-20s | %-15s | %-30s\n",
                    expense.getExpenseId(), expense.getAmount(), 
                    expense.getCategory().getCategoryName(), expense.getDate().toString(), expense.getDescription());
        }
    }

}
