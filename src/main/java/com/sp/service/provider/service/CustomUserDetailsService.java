package com.sp.service.provider.service;

import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository.findByName(name); // Find user by name
        if (user == null) {
            throw new UsernameNotFoundException("User not found with name: " + name);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getName(), // Use name as the username
                user.getPassword(),
                user.getAuthorities()
        );
    }
}
