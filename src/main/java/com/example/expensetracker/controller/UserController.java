package com.example.expensetracker.controller;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.expensetracker.dto.UserDto;
import com.example.expensetracker.model.User;
import com.example.expensetracker.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();  // fetch the logged-in user's username
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }

        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setMobile(user.getMobile());
        userDto.setUsername(user.getUsername());

        model.addAttribute("userDto", userDto);
        model.addAttribute("profilePicture", user.getProfilePicture());
        return "users/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("userDto") UserDto userDto,
                                BindingResult result,
                                Principal principal,
                                Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }

        // Optionally skip password validation if it's not being updated
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
        	 if (userDto.getPassword().length() < 6) {
        		 result.rejectValue("password", "size.userDto.password", "Password must be at least 6 characters long");
             }else {
                 result.getFieldErrors("password").clear(); // Skip password validation if empty
             }
            result.getFieldErrors("password").clear(); // Manually clear password field error
        }

        if (result.hasErrors()) {
            model.addAttribute("profilePicture", user.getProfilePicture());
            return "users/profile";
        }

        try {
            // Pass the MultipartFile and handle update in service layer
            User updatedUser = userService.updateUserProfile(userDto, user.getId());

            model.addAttribute("userDto", userDto); // Rebind updated values
            model.addAttribute("successMessage", "Profile updated successfully!");
            model.addAttribute("profilePicture", updatedUser.getProfilePicture());

            return "users/profile";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("profilePicture", user.getProfilePicture());
            return "users/profile";
        }
    }
}