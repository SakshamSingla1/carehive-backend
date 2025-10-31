package com.careHive.serviceImpl;

import com.careHive.dtos.Service.ServiceRequestDTO;
import com.careHive.dtos.Service.ServiceResponseDTO;
import com.careHive.entities.Services;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.ServiceRepository;
import com.careHive.services.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    // ✅ CREATE SERVICE
    @Override
    public ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO) throws CarehiveException {
        if (serviceRepository.findByName(serviceRequestDTO.getName()) != null) {
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_SERVICE,
                    "Service already exists with the same name");
        }

        Services service = Services.builder()
                .name(serviceRequestDTO.getName())
                .description(serviceRequestDTO.getDescription())
                .price(serviceRequestDTO.getPrice())
                .isActive(serviceRequestDTO.getIsActive())
                .build();

        Services savedService = serviceRepository.save(service);

        return mapToResponseDTO(savedService);
    }

    // ✅ UPDATE SERVICE
    @Override
    public ServiceResponseDTO updateService(String id, ServiceRequestDTO serviceRequestDTO) throws CarehiveException {
        Services existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));

        existingService.setName(serviceRequestDTO.getName());
        existingService.setDescription(serviceRequestDTO.getDescription());
        existingService.setPrice(serviceRequestDTO.getPrice());
        existingService.setIsActive(serviceRequestDTO.getIsActive());

        Services updatedService = serviceRepository.save(existingService);
        return mapToResponseDTO(updatedService);
    }

    // ✅ DELETE SERVICE
    @Override
    public String deleteService(String id) throws CarehiveException {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));

        serviceRepository.delete(service);
        return "Service deleted successfully";
    }

    // ✅ GET SERVICE BY ID
    @Override
    public ServiceResponseDTO getService(String id) throws CarehiveException {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));

        return mapToResponseDTO(service);
    }

    // ✅ GET ALL SERVICES
    @Override
    public List<ServiceResponseDTO> getAllServices() {
        List<Services> services = serviceRepository.findAll();
        return services.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // 🔹 Helper Method
    private ServiceResponseDTO mapToResponseDTO(Services service) {
        return ServiceResponseDTO.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .isActive(service.getIsActive())
                .build();
    }
}
