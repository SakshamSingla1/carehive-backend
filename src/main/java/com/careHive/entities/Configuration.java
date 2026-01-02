package com.careHive.entities;

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
@Document(collection = "configurations")
public class Configuration {
    @Id
    private String id;
    private String context;
    private String data;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
