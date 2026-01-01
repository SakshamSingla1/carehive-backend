package com.careHive.services;

import com.careHive.dtos.User.UserProfileDTO;
import com.careHive.exceptions.CarehiveException;

import java.util.List;

public interface UserService {
    List<UserProfileDTO> getAllUsers() throws CarehiveException;
}
