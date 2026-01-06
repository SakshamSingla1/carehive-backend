package com.careHive.dtos.CaretakerServices;

import com.careHive.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaretakerServiceInfoDTO {
    private String serviceId;
    private String serviceName;
    private StatusEnum status;
}
