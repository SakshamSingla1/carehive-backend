package com.careHive.dtos.NavLinks;

import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import lombok.Data;

@Data
public class NavLinkRequestDTO {
    private RoleEnum role;
    private String index;
    private String name;
    private String path;
    private StatusEnum status;
}
