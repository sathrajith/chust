package com.sp.service.provider.repository;

import com.sp.service.provider.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long>, JpaSpecificationExecutor<ServiceProvider> {
    List<ServiceProvider> findByServiceType(String serviceType);
}
