package com.hexaware.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.hexaware.dao.FinanceServicesImpl;
import com.hexaware.dto.User;
import com.hexaware.dto.Expense;
import com.hexaware.myexceptions.InvalidFieldException;
import com.hexaware.myexceptions.UserNotFoundException;
import com.hexaware.myexceptions.ExpenseNotFoundException;
import com.hexaware.util.DBConnection;

class TestFinanceServicesImpl {

    private static Connection connection;
    private FinanceServicesImpl financeService;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        connection = DBConnection.getConnection();
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        financeService = new FinanceServicesImpl(connection);
    }

    @AfterEach
    void tearDown() throws Exception {
        connection.createStatement().execute("DELETE FROM Users WHERE username LIKE 'testuser%'");
        connection.createStatement().execute("DELETE FROM Expenses WHERE description LIKE 'Test expense%'");
        
    }

    @Test
    void testCreateUser() throws SQLException, InvalidFieldException {
        User user = new User("testuser", "testpassword", "testemail@example.com");
        boolean result = financeService.createUser(user);
        assertTrue(result);
    }

    @Test
    void testCreateExpense() throws SQLException, InvalidFieldException {
        Expense expense = new Expense(1, 500.0, 1, java.sql.Date.valueOf("2024-05-20"), "Test expense");
        boolean result = financeService.createExpense(expense);
        assertTrue(result);
    }

    @Test
    void testSearchExpense() throws SQLException, ExpenseNotFoundException, InvalidFieldException {
        int userId = 1; 
        ArrayList<Expense> expenses = financeService.getAllExpensesByUser(userId);
        assertNotNull(expenses);
    }

    @Test
    void testExceptions() {
        assertThrows(UserNotFoundException.class, () -> financeService.deleteUser(1000));

        assertThrows(ExpenseNotFoundException.class, () -> financeService.getAllExpensesByUser(2000));

    }

}
