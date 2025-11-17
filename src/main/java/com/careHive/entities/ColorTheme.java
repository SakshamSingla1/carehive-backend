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
@Document(collection = "color-themes")
public class ColorTheme {
    @Id
    private String id;
    private RoleEnum role;
    private String themeName;
    private ColorPalette palette;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;

}
