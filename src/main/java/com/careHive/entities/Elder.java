package com.careHive.entities;

import com.careHive.enums.MobilityLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "elders")
public class Elder {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private List<String> medicalConditions;
    private MobilityLevelEnum mobilityLevel;
    private Boolean requiresMedication;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
