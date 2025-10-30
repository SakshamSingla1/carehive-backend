package com.careHive.dtos.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor      // generates a public no-args constructor
@AllArgsConstructor
public class RoleResponseDTO {
    private String id;
    private String name;
    private String enumCode;
}
