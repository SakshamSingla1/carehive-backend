package com.careHive.serviceImpl;

import com.careHive.dtos.User.UserProfileDTO;
import com.careHive.entities.DocumentInfo;
import com.careHive.entities.Role;
import com.careHive.entities.Users;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.RoleRepository;
import com.careHive.repositories.UserRepository;
import com.careHive.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<UserProfileDTO> getAllUsers() throws CarehiveException {

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

    private UserProfileDTO toUserProfileDTO(Users user) {

        String roleName = roleRepository
                .findByEnumCode(user.getRoleCode().name())
                .map(Role::getName)
                .orElse(null);

        return UserProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleCode(user.getRoleCode())
                .roleName(roleName)
                .phone(user.getPhone())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }



}
