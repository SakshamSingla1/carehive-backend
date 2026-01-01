package com.careHive.services;

import com.careHive.dtos.User.UserProfileRequestDTO;
import com.careHive.dtos.User.UserProfileResponseDTO;

public interface UserProfileService {
    UserProfileResponseDTO updateUserProfile(String userId,UserProfileRequestDTO dto);
    UserProfileResponseDTO getByUserId(String userId);
    void deleteByUserId(String userId);
}
