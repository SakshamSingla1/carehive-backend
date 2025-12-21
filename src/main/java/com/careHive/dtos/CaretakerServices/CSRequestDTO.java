package com.careHive.dtos.CaretakerServices;

import com.careHive.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CSRequestDTO {
    private List<String> serviceIds;
    private StatusEnum status;
}
