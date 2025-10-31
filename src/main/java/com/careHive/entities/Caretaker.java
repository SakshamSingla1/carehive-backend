package com.careHive.entities;

import com.careHive.enums.VerificationStatusEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "caretakers")
public class Caretaker {
    @Id
    private String id;
    private String name;
    private String email;
    private String username;
    private String phoneNumber;
    private boolean isVerified; // OTP verified
    private VerificationStatusEnum status; // Admin verification
    private String documentId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
