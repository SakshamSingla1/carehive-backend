package com.careHive.entities;

import com.careHive.enums.RelationShipEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "family-members")
public class FamilyMember {
    @Id
    private String id;
    private String userId;
    private String elderId;
    private RelationShipEnum relationship;
    private List<FamilyPermissionEnum> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
