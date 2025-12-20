package com.careHive.dtos.CaretakerServices;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CSRequestDTO {
    private List<String> serviceIds;
}
