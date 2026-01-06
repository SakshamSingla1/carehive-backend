package com.careHive.serviceImpl;

import com.careHive.dtos.User.UserProfileResponseDTO;
import com.careHive.entities.Elder;
import com.careHive.entities.Role;
import com.careHive.entities.UserProfile;
import com.careHive.entities.Users;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.*;
import com.careHive.services.UserService;
import com.careHive.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final ElderRepository elderRepository;
    private final Helper helper;
    private final OtpRepository otpRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public Page<UserProfileResponseDTO> findAll(
            StatusEnum status,
            RoleEnum role,
            String search,
            String sortBy,
            String sortDir,
            Pageable pageable
    ) throws CarehiveException {

        // ✅ Sorting
        Sort sort = Sort.by(
                sortDir != null && sortDir.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                sortBy != null ? sortBy : "createdAt"
        );

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Users> usersPage;

        boolean hasSearch = search != null && !search.isBlank();

        if (hasSearch && role != null && status != null) {
            usersPage = userRepository.searchUsers(search, role, status, sortedPageable);
        } else if (hasSearch && role != null) {
            usersPage = userRepository.searchUsersByRole(search, role, sortedPageable);
        } else if (hasSearch) {
            usersPage = userRepository.searchUsers(search, sortedPageable);
        } else if (role != null && status != null) {
            usersPage = userRepository.findByRoleCodeAndStatus(role, status, sortedPageable);
        } else if (role != null) {
            usersPage = userRepository.findByRoleCode(role, sortedPageable);
        } else if (status != null) {
            usersPage = userRepository.findByStatus(status, sortedPageable);
        } else {
            usersPage = userRepository.findAll(sortedPageable);
        }

        return usersPage.map(this::toUserProfileDTO);
    }

    @Override
    public void deleteUserByID(String authorizationHeader) throws CarehiveException {

        String email = helper.extractEmailFromHeader(authorizationHeader);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new CarehiveException(
                                ExceptionCodeEnum.PROFILE_NOT_FOUND,
                                "User not found"
                        )
                );

        if (user.getRoleCode() == RoleEnum.ADMIN) {
            throw new CarehiveException(
                    ExceptionCodeEnum.OPERATION_NOT_ALLOWED,
                    "Admin users cannot be deleted"
            );
        }

        String userId = user.getId();

        if (user.getRoleCode() == RoleEnum.ELDER) {
            elderRepository.deleteByUserId(userId);
        }

        userProfileRepository.findByUserId(userId)
                .ifPresent(userProfileRepository::delete);

        otpRepository.deleteByEmail(user.getEmail());
        passwordResetTokenRepository.deleteByEmail(user.getEmail());

        userRepository.delete(user);
    }

    private UserProfileResponseDTO toUserProfileDTO(Users user) {

        Role role = roleRepository.findByEnumCode(user.getRoleCode().name()).orElse(null);

        UserProfile profile = userProfileRepository
                .findByUserId(user.getId())
                .orElse(null);

        return UserProfileResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roleCode(user.getRoleCode())
                .roleName(role != null ? role.getName() : null)
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .dateOfBirth(profile != null ? profile.getDateOfBirth() : null)
                .gender(profile != null ? profile.getGender() : null)
                .address(profile != null ? profile.getAddress() : null)
                .emergencyContact(profile != null ? profile.getEmergencyContact() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
