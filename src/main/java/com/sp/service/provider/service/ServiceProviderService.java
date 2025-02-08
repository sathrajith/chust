package com.sp.service.provider.service;

import com.sp.service.provider.dto.ServiceProviderDTO;
import com.sp.service.provider.exceptiom.ResourceNotFoundException;
import com.sp.service.provider.model.ServiceProvider;
import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.ServiceProviderRepository;
import com.sp.service.provider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceProviderService {
    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private UserRepository userRepository;

    public ServiceProvider addServiceProvider(ServiceProviderDTO providerDTO) {
        User user = userRepository.findById(providerDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + providerDTO.getUserId()));

        ServiceProvider provider = new ServiceProvider();
        provider.setServiceType(providerDTO.getServiceType());
        provider.setDescription(providerDTO.getDescription());
        provider.setHourlyRate(providerDTO.getHourlyRate());
        provider.setUser(user);

        return serviceProviderRepository.save(provider);
    }

    /**
    search by type
     */
    public List<ServiceProvider> findProvidersByServiceType(String serviceType) {
        return serviceProviderRepository.findByServiceType(serviceType);
    }
    /**
    Search providers by city
     */
    public List<ServiceProvider> findProvidersByCity(String city) {
        return serviceProviderRepository.findByCity(city);
    }

    /**
    Search available providers in a city
     */
    public List<ServiceProvider> findAvailableProvidersByCity(String city) {
        return serviceProviderRepository.findByCityAndIsAvailable(city, true);
    }

    /**
    Search providers by city and minimum rating
     */
    public List<ServiceProvider> findProvidersByCityAndRating(String city, double minRating) {
        return serviceProviderRepository.findByCityAndRatingGreaterThanEqual(city, minRating);
    }

}