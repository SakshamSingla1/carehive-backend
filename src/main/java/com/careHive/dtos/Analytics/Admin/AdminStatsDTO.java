package com.careHive.dtos.Analytics.Admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStatsDTO {

    private Long totalUsers;
    private Long totalElders;
    private Long totalFamilyMembers;
    private Long totalCaretakers;

    private Long totalVerifiedCaretakers;
    private Long pendingCaretakerVerifications;

    private Long totalServices;
    private Long activeServices;
    private Long inactiveServices;

    private Long totalBookings;
    private Long pendingBookings;
    private Long completedBookings;
    private Long rejectedBookings;
    private Long inProgressBookings;
    private Long confirmedBookings;
}
