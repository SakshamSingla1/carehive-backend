package com.careHive.serviceImpl;

import com.careHive.dtos.Analytics.Admin.AdminStatsDTO;
import com.careHive.enums.BookingStatusEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.VerificationStatusEnum;
import com.careHive.repositories.BookingRepository;
import com.careHive.repositories.ServiceRepository;
import com.careHive.repositories.UserRepository;
import com.careHive.services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Override
    public AdminStatsDTO getAdminStats() {
        long totalUsers = userRepository.count();
        long totalElders = userRepository.countByRoleCode(RoleEnum.ELDER.name());
        long totalCaretakers = userRepository.countByRoleCode(RoleEnum.CARETAKER.name());
        long totalFamilyMembers = userRepository.countByRoleCode(RoleEnum.FAMILY_MEMBER.name());
        long verifiedCaretakers = userRepository.countByRoleCodeAndCaretakerStatus(RoleEnum.CARETAKER.name(), VerificationStatusEnum.APPROVED);
        long pendingCaretakers = userRepository.countByRoleCodeAndCaretakerStatus(RoleEnum.CARETAKER.name(), VerificationStatusEnum.PENDING);

        long totalServices = serviceRepository.count();
        long activeServices = serviceRepository.countByStatus(true);
        long inActiveServices = serviceRepository.countByStatus(false);

        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus(BookingStatusEnum.PENDING);
        long completedBookings = bookingRepository.countByStatus(BookingStatusEnum.COMPLETED);
        long rejectedBookings = bookingRepository.countByStatus(BookingStatusEnum.REJECTED);
        long confirmedBookings = bookingRepository.countByStatus(BookingStatusEnum.CONFIRMED);
        long inProgressBookings = bookingRepository.countByStatus(BookingStatusEnum.IN_PROGRESS);

        return AdminStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalElders(totalElders)
                .totalCaretakers(totalCaretakers)
                .totalFamilyMembers(totalFamilyMembers)
                .totalVerifiedCaretakers(verifiedCaretakers)
                .pendingCaretakerVerifications(pendingCaretakers)
                .totalServices(totalServices)
                .activeServices(activeServices)
                .inactiveServices(inActiveServices)
                .totalBookings(totalBookings)
                .pendingBookings(pendingBookings)
                .completedBookings(completedBookings)
                .rejectedBookings(rejectedBookings)
                .confirmedBookings(confirmedBookings)
                .inProgressBookings(inProgressBookings).build();
    }
}
