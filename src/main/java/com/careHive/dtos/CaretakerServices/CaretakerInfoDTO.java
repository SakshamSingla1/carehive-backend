package com.careHive.dtos.CaretakerServices;

import com.careHive.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CaretakerInfoDTO {
    private String caretakerId;
    private String caretakerName;
    private String email;
    private String phone;
    private String experienceYears;
    private List<CaretakerServiceInfoDTO> services;
    private String ratings;
}
