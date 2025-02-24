package com.sp.service.provider.repository;


import com.sp.service.provider.model.ServiceEntity;
import com.sp.service.provider.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findByProvider(User provider);
    List<ServiceEntity> findByCategory(String category);
    List<ServiceEntity> findByIsAvailableTrue();
}
