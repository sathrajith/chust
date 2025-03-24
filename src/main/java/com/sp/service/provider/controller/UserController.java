package com.sp.service.provider.controller;

import com.sp.service.provider.dto.ServiceProviderDTO;
import com.sp.service.provider.dto.UserDTO;
import com.sp.service.provider.model.User;
import com.sp.service.provider.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Value("${profile.images.path}")
    private String profileImagesPath;



    @PostMapping("/{userId}/uploadProfileImage")
    public ResponseEntity<String> uploadProfileImage(@PathVariable Long userId, @RequestParam("image") MultipartFile file) {
        try {
            // Generate a unique file name to avoid conflicts
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Ensure the directory exists
            File dir = new File(profileImagesPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Save the file to the server
            Files.copy(file.getInputStream(), Paths.get(profileImagesPath + fileName));

            // Update the user's profile image path in the database
            User user = userService.getUserById(userId);  // Get user by ID
            user.setProfileImage(fileName);  // Set the profile image path

            userService.saveUser(user);  // Save updated user

            // Respond with success
            return ResponseEntity.ok("Profile image uploaded successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload profile image: " + e.getMessage());
        }
    }

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