package com.careHive.serviceImpl;

import com.careHive.dtos.User.UserProfileResponseDTO;
import com.careHive.entities.Role;
import com.careHive.entities.UserProfile;
import com.careHive.entities.Users;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.RoleRepository;
import com.careHive.repositories.UserProfileRepository;
import com.careHive.repositories.UserRepository;
import com.careHive.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public List<UserProfileResponseDTO> getAllUsers() throws CarehiveException {

        List<Users> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new CarehiveException(
                    ExceptionCodeEnum.PROFILE_NOT_FOUND,
                    "No users found"
            );
        }

        return users.stream()
                .map(this::toUserProfileDTO)
                .toList();
    }

    private UserProfileResponseDTO toUserProfileDTO(Users user) {
        Role role = roleRepository.findByEnumCode(user.getRoleCode().name())
                .orElse(null);

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
                .roleName(role.getName())
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
