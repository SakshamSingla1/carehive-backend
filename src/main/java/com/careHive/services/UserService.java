package com.careHive.services;

import com.careHive.dtos.User.UserProfileResponseDTO;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import com.careHive.exceptions.CarehiveException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<UserProfileResponseDTO> findAll(StatusEnum status, RoleEnum role, String search,String sortBy,String sortDir,Pageable pageable) throws CarehiveException;
    void deleteUserByID(String id) throws CarehiveException;
}
