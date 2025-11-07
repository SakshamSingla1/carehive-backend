package com.careHive.services;

import com.careHive.dtos.NotificationTemplate.NTRequestDTO;
import com.careHive.dtos.NotificationTemplate.NTResponseDTO;
import com.careHive.dtos.NotificationTemplate.NotificationData;
import com.careHive.exceptions.CarehiveException;

import java.util.List;
import java.util.Map;

public interface NTService {
    NTResponseDTO createNT(NTRequestDTO ntRequestDTO) throws CarehiveException;
    NTResponseDTO updateNT(String name,NTRequestDTO ntRequestDTO) throws CarehiveException;
    String deleteNT(String name) throws CarehiveException;
    NTResponseDTO findNTBy(String name) throws CarehiveException;
    List<NTResponseDTO> findAll();
    void sendNotification(String templateName, Map<String, Object> variables, String toEmail) throws CarehiveException;
}
