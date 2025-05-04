package com.example.expensetracker.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.expensetracker.dto.ExpenseDto;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.service.ExpenseService;

import jakarta.transaction.Transactional;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    
    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }
    
    @Override
    public Expense addExpense(ExpenseDto expenseDto, User user) {
        Expense expense = new Expense();
        expense.setDescription(expenseDto.getDescription());
        expense.setAmount(expenseDto.getAmount());
        expense.setDate(expenseDto.getDate());
        expense.setCategory(expenseDto.getCategory());
        expense.setUser(user);
        
        return expenseRepository.save(expense);
    }
    
    @Override
    public Expense updateExpense(Long expenseId, ExpenseDto expenseDto, User user) {
        Expense expense = expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        expense.setDescription(expenseDto.getDescription());
        expense.setAmount(expenseDto.getAmount());
        expense.setDate(expenseDto.getDate());
        expense.setCategory(expenseDto.getCategory());
        
        return expenseRepository.save(expense);
    }
    
    @Override
    @Transactional
    public void deleteExpense(Long expenseId, User user) {
        Expense expense = expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        expenseRepository.delete(expense);
    }
    
    @Override
    public List<Expense> getAllExpenses(User user) {
        return expenseRepository.findByUser(user);
    }
    
    @Override
    public Expense getExpenseById(Long expenseId, User user) {
        return expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }
}