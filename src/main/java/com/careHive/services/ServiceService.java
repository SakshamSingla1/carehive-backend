package com.careHive.services;

import com.careHive.dtos.CaretakerServices.CSRequestDTO;
import com.careHive.dtos.CaretakerServices.CSResponseDTO;
import com.careHive.dtos.Service.ServiceRequestDTO;
import com.careHive.dtos.Service.ServiceResponseDTO;
import com.careHive.exceptions.CarehiveException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServiceService {
    ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO) throws CarehiveException;
    ServiceResponseDTO updateService(String id,ServiceRequestDTO serviceRequestDTO) throws CarehiveException;
    String deleteService(String id) throws CarehiveException;
    ServiceResponseDTO getService(String id) throws CarehiveException;
    Page<ServiceResponseDTO> getAllServices(Pageable pageable, String search, String sortBy, String sortDir) throws CarehiveException;
    List<CSResponseDTO> assignServicesToCaretaker(String caretakerId,CSRequestDTO requestDTO) throws CarehiveException;
}
