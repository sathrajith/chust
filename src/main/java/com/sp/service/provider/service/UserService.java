package com.sp.service.provider.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sp.service.provider.dto.UserDTO;
import com.sp.service.provider.exception.ResourceAlreadyExistsException;
import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    //loggers

    public User registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new ResourceAlreadyExistsException("User with email " + userDTO.getEmail() + " already exists");
        }

        User user = new User();
        user.setUser_name(userDTO.getUser_name());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRoles(Set.of("ROLE_USER")); // Default role

        return userRepository.save(user);
    }

    public User findByUserName(String username) {
        return userRepository.findByName(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByName(username) != null;
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}