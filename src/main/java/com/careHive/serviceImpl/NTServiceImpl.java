package com.careHive.serviceImpl;

import com.careHive.dtos.NavLinks.NavLinkResponseDTO;
import com.careHive.dtos.NotificationTemplate.NTRequestDTO;
import com.careHive.dtos.NotificationTemplate.NTResponseDTO;
import com.careHive.entities.NavLink;
import com.careHive.entities.NotificationTemplate;
import com.careHive.entities.Role;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.NTRepository;
import com.careHive.services.EmailService;
import com.careHive.services.NTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NTServiceImpl implements NTService {

    @Autowired
    NTRepository ntRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public NTResponseDTO createNT(NTRequestDTO ntRequestDTO) throws CarehiveException {
        if (ntRepository.existsByNameAndType(ntRequestDTO.getName(), ntRequestDTO.getType())) {
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_TEMPLATE,
                    "Template with the same name and type already exists");
        }
        NotificationTemplate template = NotificationTemplate.builder()
                .name(ntRequestDTO.getName())
                .subject(ntRequestDTO.getSubject())
                .body(ntRequestDTO.getBody())
                .type(ntRequestDTO.getType())
                .status(StatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ntRepository.save(template);
        return mapToResponseDTO(template);
    }

    @Override
    public NTResponseDTO updateNT(String name, NTRequestDTO ntRequestDTO) throws CarehiveException {
        NotificationTemplate existingTemplate = ntRepository.findByName(name)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND,
                        "Template not found for update"));

        existingTemplate.setSubject(ntRequestDTO.getSubject());
        existingTemplate.setBody(ntRequestDTO.getBody());
        existingTemplate.setStatus(ntRequestDTO.getStatus());
        existingTemplate.setUpdatedAt(LocalDateTime.now());

        ntRepository.save(existingTemplate);
        return mapToResponseDTO(existingTemplate);
    }

    @Override
    public String deleteNT(String name) throws CarehiveException {
        NotificationTemplate template = ntRepository.findByName(name)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND,
                        "Template not found for deletion"));

        ntRepository.delete(template);
        return "Template deleted successfully";
    }

    @Override
    public NTResponseDTO findNTBy(String name) throws CarehiveException {
        NotificationTemplate template = ntRepository.findByName(name)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND,
                        "Template not found"));
        return mapToResponseDTO(template);
    }

    @Override
    public Page<NTResponseDTO> getAllNotificationTemplates(
            Pageable pageable, String search, StatusEnum status, String sortBy, String sortDir) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        boolean hasStatus = status != null;
        boolean hasSearch = search != null && !search.isBlank();

        Page<NotificationTemplate> notificationTemplates;
        if( hasSearch && hasStatus){
            notificationTemplates = ntRepository.searchByStatusAndSearch(search,status,sortedPageable);
        }else if(hasSearch){
            notificationTemplates = ntRepository.SearchByText(search,sortedPageable);
        }else if(hasStatus) {
            notificationTemplates = ntRepository.findByStatus(status, sortedPageable);
        }else{
            notificationTemplates = ntRepository.findAll(sortedPageable);
        }
        return notificationTemplates.map(this::mapToResponseDTO);
    }

    @Override
    public void sendNotification(String templateName, Map<String, Object> variables, String toEmail) throws CarehiveException {
        NotificationTemplate template = ntRepository.findByName(templateName)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND,"Template not found: " + templateName));
        String body = template.getBody();
        String subject = template.getSubject();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = "{{" + entry.getKey() + "}}";
            subject = subject.replace(key, String.valueOf(entry.getValue()));
            body = body.replace(key, String.valueOf(entry.getValue()));
        }
        emailService.sendEmail(toEmail, subject, body);
    }

    private NTResponseDTO mapToResponseDTO(NotificationTemplate template) {
        return NTResponseDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .subject(template.getSubject())
                .body(template.getBody())
                .type(template.getType())
                .status(template.getStatus())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
