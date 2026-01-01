package com.careHive.entities;

import com.careHive.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user-profiles")
public class UserProfile {
    private String userId;
    private LocalDate dateOfBirth;
    private GenderEnum gender;
    private Address address;
    private EmergencyContact emergencyContact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
