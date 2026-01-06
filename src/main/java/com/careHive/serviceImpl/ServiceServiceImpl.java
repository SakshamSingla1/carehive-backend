package com.careHive.serviceImpl;

import com.careHive.dtos.CaretakerServices.CSRequestDTO;
import com.careHive.dtos.CaretakerServices.CSResponseDTO;
import com.careHive.dtos.CaretakerServices.CaretakerInfoDTO;
import com.careHive.dtos.CaretakerServices.CaretakerServiceInfoDTO;
import com.careHive.dtos.Service.ServiceRequestDTO;
import com.careHive.dtos.Service.ServiceResponseDTO;
import com.careHive.entities.CaretakerServices;
import com.careHive.entities.Services;
import com.careHive.entities.Users;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.CaretakerServicesRepository;
import com.careHive.repositories.ServiceRepository;
import com.careHive.repositories.UserRepository;
import com.careHive.services.NTService;
import com.careHive.services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NTService ntService;

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
            Pageable pageable, String search, String sortBy, String sortDir, StatusEnum status) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        Page<Services> services;
        boolean hasSearch = search != null  && !search.trim().isEmpty();
        boolean hasStatus = status != null;

        if(hasSearch && hasStatus){
            services = serviceRepository.searchByTextAndStatus(search.trim(), status, sortedPageable);
        }else if(hasSearch){
            services = serviceRepository.searchServices(search.trim(), sortedPageable);
        }else if(hasStatus){
            services = serviceRepository.findByStatus(status, sortedPageable);
        }else{
            services = serviceRepository.findAll(sortedPageable);
        }
        return services.map(this:: mapToResponseDTO);
    }

    @Override
    public List<CSResponseDTO> assignServicesToCaretaker(String caretakerId, CSRequestDTO requestDTO) throws CarehiveException {
        if (requestDTO == null || requestDTO.getServiceIds() == null || requestDTO.getServiceIds().isEmpty()) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "ServiceIds cannot be null or empty");
        }
        Users caretaker = userRepository.findByIdAndRoleCode(caretakerId, RoleEnum.CARETAKER)
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

    @Override
    public Page<CaretakerInfoDTO> getCaretakersByServiceId(
            String serviceId,
            Pageable pageable
    ) {

        // Step 1: Paginated caretaker-service mapping
        Page<CaretakerServices> csPage =
                caretakerServicesRepository.findByServiceId(serviceId, pageable);

        List<String> caretakerIds = csPage.getContent()
                .stream()
                .map(CaretakerServices::getCaretakerId)
                .toList();

        if (caretakerIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Step 2: Fetch caretakers
        Map<String, Users> caretakers =
                userRepository.findByIdIn(caretakerIds)
                        .stream()
                        .collect(Collectors.toMap(Users::getId, u -> u));

        // Step 3: Fetch ALL services offered by these caretakers
        List<CaretakerServices> allMappings =
                caretakerServicesRepository.findByCaretakerIdIn(caretakerIds);

        // Step 4: Fetch service names
        Set<String> serviceIds = allMappings.stream()
                .map(CaretakerServices::getServiceId)
                .collect(Collectors.toSet());

        Map<String, Services> serviceMap =
                serviceRepository.findByIdIn(new ArrayList<>(serviceIds))
                        .stream()
                        .collect(Collectors.toMap(Services::getId, s -> s));

        // Step 5: Group services by caretaker
        Map<String, List<CaretakerServiceInfoDTO>> servicesByCaretaker =
                allMappings.stream()
                        .collect(Collectors.groupingBy(
                                CaretakerServices::getCaretakerId,
                                Collectors.mapping(cs ->
                                                CaretakerServiceInfoDTO.builder()
                                                        .serviceId(cs.getServiceId())
                                                        .serviceName(
                                                                serviceMap.get(cs.getServiceId()).getName()
                                                        )
                                                        .status(cs.getStatus())
                                                        .build(),
                                        Collectors.toList())
                        ));

        // Step 6: Build final paged DTO
        return csPage.map(cs -> {
            Users user = caretakers.get(cs.getCaretakerId());

            return CaretakerInfoDTO.builder()
                    .caretakerId(user.getId())
                    .caretakerName(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .experienceYears("0")
                    .services(
                            servicesByCaretaker.getOrDefault(user.getId(), List.of())
                    )
                    .ratings("0") // plug rating later
                    .build();
        });
    }

    private void notifyCaretakers(Services service) {
        userRepository.findAllByRoleCode(RoleEnum.CARETAKER)
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
