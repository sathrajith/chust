package com.sp.service.provider.controller;

import com.sp.service.provider.model.User;
import com.sp.service.provider.service.CustomUserDetailsService;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Check if the username already exists
            if (userService.existsByUsername(user.getName())) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

//            if (user.getRoles() == null || user.getRoles().isEmpty()) {
//                user.setRoles(Set.of("ROLE_USER")); // Default role
//            }
//
            // Create a new user
            User newUser = new User();
            newUser.setName(user.getName());
            newUser.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
            newUser.setRoles(user.getRoles()); // Set roles (e.g., "ROLE_USER", "ROLE_ADMIN")

            // Save the user to the database
            userService.save(newUser);

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword())
            );

            // Load user details and generate tokens
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getName());
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // Set access token in HTTP-only cookie
            Cookie accessTokenCookie = createCookie("access_token", accessToken, 15 * 60); // 15 minutes
            response.addCookie(accessTokenCookie);

            // Set refresh token in HTTP-only cookie
            Cookie refreshTokenCookie = createCookie("refresh_token", refreshToken, 7 * 24 * 60 * 60); // 7 days
            response.addCookie(refreshTokenCookie);

            // Return success response
            Map<String, Object> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            tokens.put("roles", userDetails.getAuthorities());
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Extract refresh token from cookies
            String refreshToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> "refresh_token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token missing");
            }

            // Validate refresh token
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtUtil.validateToken(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
            }

            // Generate new access token
            String newAccessToken = jwtUtil.generateToken(userDetails);

            // Set new access token in HTTP-only cookie
            Cookie accessTokenCookie = createCookie("access_token", newAccessToken, 15 * 60); // 15 minutes
            response.addCookie(accessTokenCookie);

            return ResponseEntity.ok("Token refreshed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token refresh failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Invalidate access token cookie
        Cookie accessTokenCookie = createCookie("access_token", null, 0);
        response.addCookie(accessTokenCookie);

        // Invalidate refresh token cookie
        Cookie refreshTokenCookie = createCookie("refresh_token", null, 0);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is required");
        }

        try {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                return ResponseEntity.ok("Token is valid");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validation failed: " + e.getMessage());
        }
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Enable in production (HTTPS only)
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Strict"); // Prevent CSRF attacks
        return cookie;
    }
}