package com.careHive.services;

import com.careHive.dtos.User.UserProfileResponseDTO;
import com.careHive.exceptions.CarehiveException;

import java.util.List;

public interface UserService {
    List<UserProfileResponseDTO> getAllUsers() throws CarehiveException;
}
