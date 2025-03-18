package com.sp.service.provider.controller;

import com.sp.service.provider.dto.ServiceProviderDTO;
import com.sp.service.provider.dto.UserDTO;
import com.sp.service.provider.model.User;
import com.sp.service.provider.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

//    @PostMapping("/register")
//    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDTO userDTO) {
//        User user = userService.registerUser(userDTO);
//        return ResponseEntity.ok(user);
//    }

    //@PreAuthorize("hasAnyRole('USER', 'PROVIDER', 'ADMIN')")
    @GetMapping("/providers")
    public ResponseEntity<List<ServiceProviderDTO>> getServiceProviders() {
        List<ServiceProviderDTO> providers = userService.getServiceProviders();
        return ResponseEntity.ok(providers);
    }


    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}/addRole")
    public ResponseEntity<String> addRole(@PathVariable String username, @RequestParam String role) {
        userService.addRoleToUser(username, role);
        return ResponseEntity.ok("Role " + role + " added to user " + username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}/removeRole")
    public ResponseEntity<String> removeRole(@PathVariable String username, @RequestParam String role) {
        userService.removeRoleFromUser(username, role);
        return ResponseEntity.ok("Role " + role + " removed from user " + username);
    }
}