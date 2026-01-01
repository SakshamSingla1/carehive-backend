package com.careHive.serviceImpl;

import com.careHive.dtos.User.UserProfileRequestDTO;
import com.careHive.dtos.User.UserProfileResponseDTO;
import com.careHive.services.UserProfileService;

public class UserProfileServiceImpl implements UserProfileService {
    @Override
    public UserProfileResponseDTO updateUserProfile(String userId, UserProfileRequestDTO dto) {
        return null;
    }

    @Override
    public UserProfileResponseDTO getByUserId(String userId) {
        return null;
    }

    @Override
    public void deleteByUserId(String userId) {

    }
}
