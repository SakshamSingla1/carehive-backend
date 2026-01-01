package com.careHive.controller;

import com.careHive.dtos.User.UserProfileResponseDTO;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ResponseModel<List<UserProfileResponseDTO>>> getUsers() throws CarehiveException {
        List<UserProfileResponseDTO> responseList = userService.getAllUsers();
        return ApiResponse.respond(responseList,"Users fetched successfully","Unable to fetch users");
    }
}
