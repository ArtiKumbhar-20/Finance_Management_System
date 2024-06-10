package com.hexaware.myexceptions;

public class ExpenseNotFoundException extends Exception {
	public ExpenseNotFoundException(String message) {
		super(message);
	}
}
