package com.hexaware.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.hexaware.dao.FinanceServicesImpl;
import com.hexaware.dto.Expense;
import com.hexaware.dto.ExpenseCategory;
import com.hexaware.dto.User;
import com.hexaware.myexceptions.ExpenseNotFoundException;
import com.hexaware.myexceptions.InvalidFieldException;
import com.hexaware.myexceptions.UserNotFoundException;
import com.hexaware.util.DBConnection;

public class MainMod {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection connection = DBConnection.getConnection();
        FinanceServicesImpl financeService = new FinanceServicesImpl(connection);

        while (true) {
            System.out.println("\nFinance Management System");
            System.out.println("------------------------");
            System.out.println("1. Add User");
            System.out.println("2. Add Expense");
            System.out.println("3. Delete User");
            System.out.println("4. Delete Expense");
            System.out.println("5. Update Expense");
            System.out.println("6. Display All Expenses of User");
            System.out.println("7. Exit");
            System.out.println("Enter your choice:");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    System.out.println("Enter User details:");
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();

                    try {
                        User user = new User(username, password, email);
                        boolean userAdded = financeService.createUser(user);
                        if (userAdded) {
                            System.out.println("User added successfully.");
                        } else {
                            System.out.println("Failed to add user.");
                        }
                    } catch (SQLException | InvalidFieldException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.println("Enter Expense details:");
                    System.out.print("User ID: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine(); 

                    System.out.print("Amount: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine(); 

                    System.out.println("Category ID Options:");
                    System.out.println("------------------------");
                    System.out.println("1. Food");
                    System.out.println("2. Transportation");
                    System.out.println("3. Entertainment");
                    System.out.println("4. Utilities");
                    System.out.println("5. Shopping");
                    System.out.println("------------------------");
                    System.out.print("Enter Category ID: ");
                    int categoryId = scanner.nextInt();
                    scanner.nextLine(); 
                    System.out.print("Date (YYYY-MM-DD): ");
                    String dateStr = scanner.nextLine();
                    Date date = java.sql.Date.valueOf(dateStr);
                    System.out.print("Description: ");
                    String description = scanner.nextLine();

                    try {
                        Expense expense = new Expense(userId, amount, categoryId, date, description);
                        boolean expenseAdded = financeService.createExpense(expense);
                        if (expenseAdded) {
                            System.out.println("Expense added successfully.");
                        } else {
                            System.out.println("Failed to add expense.");
                        }
                    } catch (SQLException | InvalidFieldException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.print("Enter User ID to delete: ");
                    int deleteUserId = scanner.nextInt();
                    scanner.nextLine(); 

                    try {
                        boolean userDeleted = financeService.deleteUser(deleteUserId);
                        if (userDeleted) {
                            System.out.println("User deleted successfully.");
                        } else {
                            System.out.println("Failed to delete user.");
                        }
                    } catch (SQLException | UserNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 4:                   
                    System.out.print("Enter Expense ID to delete: ");
                    int deleteExpenseId = scanner.nextInt();
                    scanner.nextLine(); 

                    try {
                        boolean expenseDeleted = financeService.deleteExpense(deleteExpenseId);
                        if (expenseDeleted) {
                            System.out.println("Expense deleted successfully.");
                        } else {
                            System.out.println("Failed to delete expense.");
                        }
                    } catch (SQLException | ExpenseNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 5:                    
                    System.out.println("Enter User ID:");
                    int updateUserId = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        financeService.getAllExpensesByUser(updateUserId);
                    } catch (ExpenseNotFoundException | SQLException | InvalidFieldException e) {
                        System.out.println("Error: " + e.getMessage());
                    }

                    System.out.println("Enter Expense ID to update:");
                    int updateExpenseId = scanner.nextInt();
                    scanner.nextLine(); 

                    System.out.println("Enter new Amount:");
                    double newAmount = scanner.nextDouble();
                    scanner.nextLine(); 
                    
                    System.out.println("Category ID Options:");
                    System.out.println("------------------------");
                    System.out.println("1. Food");
                    System.out.println("2. Transportation");
                    System.out.println("3. Entertainment");
                    System.out.println("4. Utilities");
                    System.out.println("5. Shopping");
                    System.out.println("------------------------");
                    System.out.println("Enter new Category ID:");
                    int newCategoryId = scanner.nextInt();
                    scanner.nextLine(); 

                    System.out.println("Enter new Date (YYYY-MM-DD):");
                    String newDateStr = scanner.nextLine();
                    Date newDate = java.sql.Date.valueOf(newDateStr);

                    System.out.println("Enter new Description:");
                    String newDescription = scanner.nextLine();

                    try {
                        Expense updatedExpense = new Expense(updateExpenseId, updateUserId, newAmount, newCategoryId, newDate, newDescription);
                        boolean expenseUpdated = financeService.updateExpense(updateUserId, updatedExpense);
                        if (expenseUpdated) {
                            System.out.println("Expense updated successfully.");
                        } else {
                            System.out.println("Failed to update expense.");
                        }
                    } catch (SQLException | ExpenseNotFoundException | InvalidFieldException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 6:
                    System.out.print("Enter User ID to display expenses: ");
                    int userId1 = scanner.nextInt();
                    try {
                        financeService.getAllExpensesByUser(userId1);
                    } catch (ExpenseNotFoundException | SQLException | InvalidFieldException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 7:
                    // Exit
                    System.out.println("Exiting Finance Management System.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
                    break;
            }
        }
    }
}
