package com.example.expensetracker.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.expensetracker.dto.ExpenseDto;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.UserRepository;
import com.example.expensetracker.service.ExpenseService;
import com.example.expensetracker.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    
    @GetMapping
    public String listExpenses(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Expense> expenses = expenseService.getAllExpenses(user);
        model.addAttribute("expenses", expenses);
        return "expenses/list-expenses";
    }
    
    @GetMapping("/add")
    public String showAddExpenseForm(Model model) {
        model.addAttribute("expenseDto", new ExpenseDto());
        return "expenses/add-expense";
    }
    
    @PostMapping("/add")
    public String addExpense(@Valid @ModelAttribute("expenseDto") ExpenseDto expenseDto,
                             BindingResult result,
                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        if (result.hasErrors()) {
            return "expenses/add-expense";
        }

        // Get the username from Security's User
        String username = userDetails.getUsername();

        // Look up the full User entity from your DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        expenseService.addExpense(expenseDto, user);
        return "redirect:/expenses";
    }

    
    @GetMapping("/edit/{id}")
    public String showEditExpenseForm(@PathVariable Long id,
                                      Principal principal,
                                      Model model) {
        // Get username from the logged-in principal
        String username = principal.getName();

        // Fetch the User entity using your UserRepository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));


        // Now retrieve the expense
        Expense expense = expenseService.getExpenseById(id, user);

        // Populate the DTO
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setId(expense.getId());
        expenseDto.setDescription(expense.getDescription());
        expenseDto.setAmount(expense.getAmount());
        expenseDto.setDate(expense.getDate());
        expenseDto.setCategory(expense.getCategory());

        model.addAttribute("expenseDto", expenseDto);
        return "expenses/edit-expense";
    }

    
    @PostMapping("/edit/{id}")
    public String updateExpense(@PathVariable Long id,
                                @Valid @ModelAttribute("expenseDto") ExpenseDto expenseDto,
                                BindingResult result,
                                Principal principal) {
        if (result.hasErrors()) {
            return "expenses/edit-expense";
        }

        String username = principal.getName(); // this is usually the user's email or username

        // Now fetch the User entity using the username
        User user = userService.getUserByUsername(username); // make sure this method exists

        expenseService.updateExpense(id, expenseDto, user);
        return "redirect:/expenses";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, Principal principal, Model model) {
        // Ensure the 'user' variable is properly initialized
        if (principal == null) {
            return "redirect:/login"; // Handle the case where the user is not logged in
        }
        String username = principal.getName();  // Get the username from Principal
        User user = userService.getUserByUsername(username);
        try {
            // Delete the expense by its ID
            expenseService.deleteExpense(id,user);

            // Redirect to a page, such as the list of expenses
            return "redirect:/expenses";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while deleting the expense.");
            return "error";
        }
    }
}