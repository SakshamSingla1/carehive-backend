package com.careHive.serviceImpl;

import com.careHive.dtos.NotificationTemplate.NTRequestDTO;
import com.careHive.dtos.NotificationTemplate.NTResponseDTO;
import com.careHive.entities.NotificationTemplate;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.NTRepository;
import com.careHive.services.EmailService;
import com.careHive.services.NTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NTServiceImpl implements NTService {

    @Autowired
    NTRepository ntRepository;
    @Autowired
    private EmailService emailService;

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
                .active(true)
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
        existingTemplate.setActive(ntRequestDTO.isActive());
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
    public List<NTResponseDTO> findAll() {
        return ntRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
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
                .active(template.isActive())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
