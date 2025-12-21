package com.careHive.dtos.Analytics.Admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStatsDTO {

    // -------------------------
    // USER STATS
    // -------------------------
    private Long totalUsers;
    private Long totalElders;
    private Long totalFamilyMembers;
    private Long totalCaretakers;

    private Long totalVerifiedCaretakers;
    private Long pendingCaretakerVerifications;

    // -------------------------
    // SERVICE STATS
    // -------------------------
    private Long totalServices;
    private Long activeServices;
    private Long inactiveServices;

    // -------------------------
    // BOOKING STATS
    // -------------------------
    private Long totalBookings;
    private Long pendingBookings;
    private Long completedBookings;
    private Long rejectedBookings;
    private Long inProgressBookings;
    private Long confirmedBookings;
}
