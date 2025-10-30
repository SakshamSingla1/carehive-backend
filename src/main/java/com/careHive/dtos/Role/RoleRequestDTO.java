package com.careHive.dtos.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleRequestDTO {
    private String name;
    private String enumCode;
}
