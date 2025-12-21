package com.careHive.serviceImpl;

import com.careHive.dtos.CaretakerServices.CSRequestDTO;
import com.careHive.dtos.CaretakerServices.CSResponseDTO;
import com.careHive.dtos.Service.ServiceRequestDTO;
import com.careHive.dtos.Service.ServiceResponseDTO;
import com.careHive.entities.CaretakerServices;
import com.careHive.entities.Services;
import com.careHive.entities.User;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.CaretakerServicesRepository;
import com.careHive.repositories.ServiceRepository;
import com.careHive.repositories.UserRepository;
import com.careHive.services.NTService;
import com.careHive.services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NTService ntService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CaretakerServicesRepository caretakerServicesRepository;

    @Override
    public ServiceResponseDTO createService(ServiceRequestDTO dto) throws CarehiveException {
        if (serviceRepository.findByName(dto.getName()) != null) {
            throw new CarehiveException(
                    ExceptionCodeEnum.DUPLICATE_SERVICE,
                    "Service already exists with the same name"
            );
        }
        Services service = Services.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .pricePerHour(dto.getPricePerHour())
                .status(dto.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        serviceRepository.save(service);
        notifyCaretakers(service);
        return mapToResponseDTO(service);
    }

    @Override
    public ServiceResponseDTO updateService(String id, ServiceRequestDTO dto) throws CarehiveException {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));

        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setPricePerHour(dto.getPricePerHour());
        service.setStatus(dto.getStatus());
        service.setUpdatedAt(LocalDateTime.now());
        serviceRepository.save(service);
        return mapToResponseDTO(service);
    }

    @Override
    public String deleteService(String id) throws CarehiveException {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));
        serviceRepository.delete(service);
        return "Service deleted successfully";
    }

    @Override
    public ServiceResponseDTO getService(String id) throws CarehiveException {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));
        return mapToResponseDTO(service);
    }

    @Override
    public Page<ServiceResponseDTO> getAllServices(
            Pageable pageable, String search, String sortBy, String sortDir) {
        Pageable p = PageRequest.of(
                pageable == null ? 0 : pageable.getPageNumber(),
                pageable == null ? 20 : pageable.getPageSize(),
                Sort.by(
                        "desc".equalsIgnoreCase(sortDir)
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC,
                        (sortBy == null || sortBy.isBlank()) ? "name" : sortBy
                )
        );
        Query q = new Query().with(p);
        if (search != null && !search.isBlank()) {
            String regex = ".*" + Pattern.quote(search) + ".*";
            q.addCriteria(
                    Criteria.where("name").regex(regex, "i")
            );
        }
        long total = mongoTemplate.count(q, Services.class);
        return new PageImpl<>(
                mongoTemplate.find(q, Services.class)
                        .stream()
                        .map(this::mapToResponseDTO)
                        .toList(),
                p,
                total
        );
    }

    @Override
    public List<CSResponseDTO> assignServicesToCaretaker(String caretakerId, CSRequestDTO requestDTO) throws CarehiveException {
        if (requestDTO == null || requestDTO.getServiceIds() == null || requestDTO.getServiceIds().isEmpty()) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "ServiceIds cannot be null or empty");
        }
        User caretaker = userRepository.findByIdAndRoleCode(caretakerId, RoleEnum.CARETAKER.name())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not present"));
        List<Services> services =
                serviceRepository.findAllById(requestDTO.getServiceIds());
        if (services.size() != requestDTO.getServiceIds().size()) {
            throw new CarehiveException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "One or more services not found");
        }
        List<CSResponseDTO> response = new ArrayList<>();
        for (Services service : services) {
            boolean exists = caretakerServicesRepository.findByCaretakerIdAndServiceId(caretakerId, service.getId()).isPresent();
            if (!exists) {
                CaretakerServices mapping = caretakerServicesRepository.save(
                        CaretakerServices.builder()
                                .caretakerId(caretakerId)
                                .serviceId(service.getId())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .status(requestDTO.getStatus())
                                .build()
                );
                response.add(
                        CSResponseDTO.builder()
                                .id(mapping.getId())
                                .caretakerId(caretakerId)
                                .caretakerName(caretaker.getName())
                                .serviceId(service.getId())
                                .serviceName(service.getName())
                                .status(service.getStatus())
                                .createdAt(mapping.getCreatedAt())
                                .updatedAt(mapping.getUpdatedAt())
                                .build()
                );
            }
        }
        return response;
    }

    // 🔔 NOTIFY CARETAKERS
    private void notifyCaretakers(Services service) {
        userRepository.findAllByRoleCode(RoleEnum.CARETAKER.name())
                .forEach(caretaker -> {
                    Map<String, Object> vars = new HashMap<>();
                    vars.put("caretakerName", caretaker.getName());
                    vars.put("serviceName", service.getName());
                    vars.put("description", service.getDescription());
                    vars.put("pricePerHour", service.getPricePerHour());
                    vars.put("isActive", service.getStatus());
                    vars.put("dashboardLink", "https://app.carehive.com/dashboard");

                    try {
                        ntService.sendNotification(
                                "NEW-SERVICE-NOTIFICATION-CARETAKER",
                                vars,
                                caretaker.getEmail()
                        );
                    } catch (CarehiveException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    // 🔹 MAPPER
    private ServiceResponseDTO mapToResponseDTO(Services service) {
        return ServiceResponseDTO.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .pricePerHour(service.getPricePerHour())
                .status(service.getStatus())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
    }
}
