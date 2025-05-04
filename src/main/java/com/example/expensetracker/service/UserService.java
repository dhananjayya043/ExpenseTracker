package com.example.expensetracker.service;

import com.example.expensetracker.dto.UserDto;
import com.example.expensetracker.model.User;

public interface UserService {
    User registerUser(UserDto userDto);
    User updateUserProfile(UserDto userDto, Long userId);
    User getUserById(Long userId);
    User getUserByUsername(String username);
    
}