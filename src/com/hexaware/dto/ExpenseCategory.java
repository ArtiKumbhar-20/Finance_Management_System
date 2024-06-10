package com.hexaware.dto;

import java.util.ArrayList;
import java.util.List;

public class ExpenseCategory {
	private int categoryId;
	private String categoryName;
	private List<Expense> expenses;
	
	public ExpenseCategory() {
		
	}

	public ExpenseCategory(int categoryId, String categoryName) {
		super();
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.expenses = new ArrayList<>();
	}

	public List<Expense> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Expense> expenses) {
		this.expenses = expenses;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public String toString() {
		return "ExpenseCategory [categoryId=" + categoryId + ", categoryName=" + categoryName + "]";
	}
	
}
