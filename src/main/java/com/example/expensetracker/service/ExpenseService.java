package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseDto;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import java.util.List;

public interface ExpenseService {
    Expense addExpense(ExpenseDto expenseDto, User user);
    Expense updateExpense(Long expenseId, ExpenseDto expenseDto, User user);
    void deleteExpense(Long expenseId, User user);
    List<Expense> getAllExpenses(User user);
    Expense getExpenseById(Long expenseId, User user);
    
    
}