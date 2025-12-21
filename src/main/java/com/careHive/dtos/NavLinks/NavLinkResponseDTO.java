package com.careHive.dtos.NavLinks;

import com.careHive.enums.RoleEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NavLinkResponseDTO {
    private String id;
    private RoleEnum roleCode;
    private String index;
    private String name;
    private String path;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
