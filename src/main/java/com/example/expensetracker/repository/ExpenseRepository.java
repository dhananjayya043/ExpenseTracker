package com.example.expensetracker.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;

		public interface ExpenseRepository extends JpaRepository<Expense, Long> {
		    List<Expense> findByUser(User user);
		    List<Expense> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
		    List<Expense> findByUserAndCategory(User user, String category);
		    Optional<Expense> findByIdAndUser(Long id, User user);
		    List<Expense> findByUserUsername(String username);
		}