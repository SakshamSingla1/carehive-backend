package com.careHive.dtos.NavLinks;

import com.careHive.enums.RoleEnum;
import lombok.Data;

@Data
public class NavLinkRequestDTO {
    private RoleEnum roleCode;
    private String index;
    private String name;
    private String path;
}
