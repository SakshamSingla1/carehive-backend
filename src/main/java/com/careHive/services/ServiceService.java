package com.careHive.services;

import com.careHive.dtos.Service.ServiceRequestDTO;
import com.careHive.dtos.Service.ServiceResponseDTO;
import com.careHive.exceptions.CarehiveException;

import java.util.List;

public interface ServiceService {
    ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO) throws CarehiveException;
    ServiceResponseDTO updateService(String id,ServiceRequestDTO serviceRequestDTO) throws CarehiveException;
    String deleteService(String id) throws CarehiveException;
    ServiceResponseDTO getService(String id) throws CarehiveException;
    List<ServiceResponseDTO> getAllServices() throws CarehiveException;
}
