package com.careHive.dtos.Elder;

import com.careHive.enums.MobilityLevelEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ElderDTO {
    private List<String> medicalConditions;
    private MobilityLevelEnum mobilityLevel;
    private Boolean requiresMedication;
}
