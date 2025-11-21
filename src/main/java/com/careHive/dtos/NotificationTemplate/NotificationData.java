package com.careHive.dtos.NotificationTemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationData {
    private String name;
    private String email;
    private String amount;
    private String date;
    private String bookingId;
}
