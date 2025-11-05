package com.careHive.entities;

import com.careHive.enums.RoleEnum;
import com.careHive.enums.VerificationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String username;
    private String email;
    private String password;
    private String roleCode;
    private String phoneNumber;
    private boolean isVerified;
    private List<String> serviceIds; // List of service references
    private VerificationStatusEnum caretakerStatus; // Admin verification
    private List<DocumentInfo> documents; // <-- multiple uploaded documents
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
