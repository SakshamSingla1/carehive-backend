package com.careHive.dtos.NavLinks;

import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NavLinkResponseDTO {
    private String id;
    private RoleEnum role;
    private String index;
    private String name;
    private String path;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
