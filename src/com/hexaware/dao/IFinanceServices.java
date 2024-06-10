package com.hexaware.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hexaware.dto.Expense;
import com.hexaware.dto.User;
import com.hexaware.myexceptions.ExpenseNotFoundException;
import com.hexaware.myexceptions.InvalidFieldException;
import com.hexaware.myexceptions.UserNotFoundException;

public interface IFinanceServices {
	
	boolean createUser(User user) throws SQLException, UserNotFoundException, InvalidFieldException;
	
	boolean createExpense(Expense expense) throws SQLException, InvalidFieldException;
	
	boolean deleteUser(int userId) throws SQLException, UserNotFoundException, InvalidFieldException;
	
	boolean deleteExpense(int expenseId) throws SQLException, ExpenseNotFoundException, InvalidFieldException;
	
	
	boolean updateExpense(int userId, Expense expense) throws SQLException, ExpenseNotFoundException, InvalidFieldException;

	ArrayList<Expense> getAllExpensesByUser(int userId) throws SQLException, ExpenseNotFoundException, InvalidFieldException;
	
}
