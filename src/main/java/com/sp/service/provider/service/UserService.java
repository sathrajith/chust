package com.sp.service.provider.service;

import com.sp.service.provider.dto.UserDTO;
import com.sp.service.provider.exceptiom.ResourceAlreadyExistsException;
import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with email " + userDTO.getEmail() + " already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRoles(Set.of("ROLE_USER"));

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
/*
role to user
 */
@Transactional
public void addRoleToUser(String username, String role) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (user.getRoles() == null) {
        user.setRoles(new HashSet<>()); // Ensure roles are initialized
    }
    user.getRoles().add(role); // Directly modify the set
    userRepository.save(user);
}

    @Transactional
    public void removeRoleFromUser(String username, String role) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getRoles() != null && user.getRoles().contains(role)) {
            user.getRoles().remove(role); // Directly modify the set
            userRepository.save(user);
        }
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveResetToken(String email, String token) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setResetToken(token);
        userRepository.save(user);
    }
    public Optional<User> findByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }



    // Load user details from the database
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    public void saveUser(User user) {
        userRepository.save(user);
    }


}