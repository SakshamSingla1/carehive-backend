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
public class Users {
    @Id
    private String id;
    private String name;
    private String username;
    private String email;
    private String password;
    private RoleEnum roleCode;
    private String phone;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
