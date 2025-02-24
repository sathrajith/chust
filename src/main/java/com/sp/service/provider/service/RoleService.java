package com.sp.service.provider.service;

import com.sp.service.provider.exceptiom.ResourceAlreadyExistsException;
import com.sp.service.provider.model.Role;
import com.sp.service.provider.model.RoleName;
import com.sp.service.provider.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Cacheable("roles") // ✅ Enables caching of roles
    @Transactional(readOnly = true)
    public Role getRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    }

    /**
     * Checks if a role exists, used for initialization.
     */
    public boolean roleExists(RoleName roleName) {
        return roleRepository.findByName(roleName).isPresent();
    }

    /**
     * Saves a new role if it doesn’t exist.
     */
    public void saveRole(Role role) {
        roleRepository.save(role);
    }


//    public void updateUserRole(String Role){
//        if(!User.role = null){
//            return new ResourceAlreadyExistsException("role already exist");
//        }
//    }
}
