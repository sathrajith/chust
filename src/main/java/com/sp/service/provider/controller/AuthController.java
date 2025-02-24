package com.sp.service.provider.controller;

import com.sp.service.provider.dto.UserDTO;
import com.sp.service.provider.model.User;
import com.sp.service.provider.service.EmailService;
import com.sp.service.provider.service.UserService;
import com.sp.service.provider.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * User Registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.registerUser(userDTO);

            String subject = " Welcome to chusit,";
            String text = "Dear "+ user.getUsername() + " thank you for choosing us";
            emailService.sendEmail(user.getEmail(), subject, text);
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }



    /**
     * Login Endpoint - Generates JWT Tokens
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Missing username or password");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = userService.loadUserByUsername(username);
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        addCookie(response, "refresh_token", refreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return ResponseEntity.ok(tokens);
    }


    /**
     * Logout Endpoint - Clears authentication cookies
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        removeCookie(response, "access_token");
        removeCookie(response, "refresh_token");
        return ResponseEntity.ok("Logged out successfully!");
    }

    /**
     * Refresh Token Endpoint - Generates new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request, "refresh_token");
        if (refreshToken == null || !jwtUtil.validateRefreshToken(refreshToken)) {
            return ResponseEntity.badRequest().body("Invalid or expired refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        UserDetails userDetails = userService.loadUserByUsername(username);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails.getUsername()); // Use username

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", newAccessToken);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> user = userService.findByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
        }

        // Generate token
        String token = UUID.randomUUID().toString();
        userService.saveResetToken(email, token);

        // Send email
        String subject = "Password Reset Request";
        String text = "To reset your password, click the link: http://yourapp.com/reset-password?token=" + token;
        emailService.sendEmail(email, subject, text);

        return ResponseEntity.ok(Map.of("message", "Password reset email sent!"));
    }


    /**
     * Password Reset Endpoint
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        Optional<User> user = userService.findByResetToken(token);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        User updatedUser = user.get();
        updatedUser.setPassword(passwordEncoder.encode(newPassword));
        updatedUser.setResetToken(null); // Clear token after reset
        userService.saveUser(updatedUser);

        String subject = "Password Reset Confirmation";
        String text = "Your password has been successfully reset.";
        emailService.sendEmail(updatedUser.getEmail(), subject, text);

        return ResponseEntity.ok("Password successfully reset!");
    }

    /**
     * Helper Method: Add Cookie
     */
    private void addCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);  // Use true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
    }

    /**
     * Helper Method: Remove Cookie
     */
    private void removeCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * Helper Method: Get Cookie Value
     */
    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        // Verify OTP
        try {
            String accessToken = userService.verifyOtp(email, otp);

            // Send OTP verification success email
            String subject = "OTP Verification Successful";
            String text = "Your OTP has been successfully verified.";
            emailService.sendEmail(email, subject, text);

            return ResponseEntity.ok(Map.of("message", "OTP verified successfully!", "access_token", accessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}