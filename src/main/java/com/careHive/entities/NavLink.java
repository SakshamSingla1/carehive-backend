package com.careHive.entities;

import com.careHive.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "nav-links")
public class NavLink {
    @Id
    private String id;
    private RoleEnum roleCode;
    private String index;
    private String name;
    private String path;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
