package com.sp.service.provider.service;

import com.sp.service.provider.dto.ServiceProviderDTO;
import com.sp.service.provider.dto.UserDTO;
import com.sp.service.provider.exceptiom.ResourceAlreadyExistsException;
import com.sp.service.provider.model.Role;
import com.sp.service.provider.model.RoleName;
import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.RoleRepository;
import com.sp.service.provider.repository.UserRepository;
import com.sp.service.provider.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private RoleService roleService; // Use RoleService instead of direct RoleRepository

    /**
     * Register a new user (Open to all)
     */
    public User registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setServiceType(userDTO.getServiceType());
        user.setDescription(userDTO.getDescription());
        user.setHourlyRate(userDTO.getHourlyRate());
        user.setAvailable(userDTO.isAvailable());
        user.setVerified(true);  // Bypass OTP and directly set as verified

        Set<Role> roles = new HashSet<>();
        if (userDTO.getServiceType() != null && !userDTO.getServiceType().isEmpty()) {
            System.out.println("🔍 Assigning ROLE_PROVIDER to user: " + userDTO.getUsername());
            roles.add(roleService.getRole(RoleName.ROLE_PROVIDER));
        } else {
            System.out.println("🔍 Assigning ROLE_USER to user: " + userDTO.getUsername());
            roles.add(roleService.getRole(RoleName.ROLE_USER));
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }


    /**
     * Verify OTP (Open to all)
     */
    public String verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        user.setVerified(true);
        user.setOtp(null);
        userRepository.save(user);

        return jwtUtil.generateAccessToken(user.getUsername());
    }

    /**
     * Authenticate user (Open to all)
     */
    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials: User not found"));

        System.out.println("🔍 Found User: " + user.getUsername());
        System.out.println("🔍 User Password (Encoded): " + user.getPassword());
        System.out.println("🔍 Entered Password: " + password);
        System.out.println("🔍 User Verified: " + user.isVerified());
        System.out.println("🔍 User Roles: " + user.getRoles());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials: Password mismatch");
        }

//        if (!user.isVerified()) {
//            throw new RuntimeException("Account not verified. Check your email for OTP.");
//        }

        String token = jwtUtil.generateAccessToken(user.getUsername());
        System.out.println("✅ Authentication Successful! Token: " + token);
        return token;
    }



    /**
     * Add role to user (Only ADMIN can assign roles)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role role = roleService.getRole(RoleName.valueOf(roleName.toUpperCase()));

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>()); // Ensure roles are initialized
        }
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void removeRoleFromUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role role = roleService.getRole(RoleName.valueOf(roleName.toUpperCase()));

        if (user.getRoles() != null && user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userRepository.save(user);
        }
    }

    /**
     * Load user by username (For authentication)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        System.out.println("🔍 Loading User for Authentication: " + user.getUsername());
        System.out.println("🔍 User Roles: " + user.getRoles());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isVerified(), // Must be true for authentication
                true, true, true,
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                        .collect(Collectors.toList()) // Ensuring role format is correct
        );
    }



    /**
     * Get all users (Only ADMIN can see all users)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Find user by username (Only ADMIN or the same user can view details)
     */
    //@PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find user by email (Only ADMIN can access)
     */
    //@PreAuthorize("hasRole('ADMIN')")
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Save reset token (Open to all)
     */
    public void saveResetToken(String email, String token) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setResetToken(token);
        userRepository.save(user);
    }

    /**
     * Find user by reset token (Only ADMIN or user themselves)
     */
    @PreAuthorize("hasRole('ADMIN') or #token == authentication.principal.resetToken")
    public Optional<User> findByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }

    /**
     * Save user (Only ADMIN can create or update users)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @PostConstruct
    public void initRoles() {
        if (!roleService.roleExists(RoleName.ROLE_USER)) {
            Role userRole = new Role();
            userRole.setName(RoleName.ROLE_USER);
            roleService.saveRole(userRole);
        }
        if (!roleService.roleExists(RoleName.ROLE_PROVIDER)) {
            Role providerRole = new Role();
            providerRole.setName(RoleName.ROLE_PROVIDER);
            roleService.saveRole(providerRole);
        }
        if (!roleService.roleExists(RoleName.ROLE_ADMIN)) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ROLE_ADMIN);
            roleService.saveRole(adminRole);
        }
        System.out.println("✅ Roles Initialized Successfully!");
    }


    /**
     * Get all service providers (Users with ROLE_PROVIDER)
     */
    public List<ServiceProviderDTO> getServiceProviders() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName() == RoleName.ROLE_PROVIDER))
                .map(user -> {
                    ServiceProviderDTO dto = new ServiceProviderDTO();
                    dto.setId(Long.valueOf(user.getId().toString()));
                    dto.setName(user.getUsername());
                    dto.setServiceType(user.getServiceType());
//                    dto.setLocation(user.getLocation()); // Assuming you have a location field in User
                    dto.setRating(user.getRating()); // Assuming you have a rating field in User
                    return dto;
                })
                .collect(Collectors.toList());
    }
//    public void updateServiceProvider(){
//        User user = new User();
//        user.setUsername(user.getUsername());
//        user.setEmail(user.getEmail());
//        user.setPhoneNumber((user.getPhoneNumber()));
//        user.setDescription((user.getDescription()));
//        user.setServiceType(user.getServiceType());
//        user.setHourlyRate(user.getHourlyRate());
//
//        return userRepository.save(User);
//    }
}
