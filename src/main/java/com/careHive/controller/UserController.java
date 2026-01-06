package com.careHive.controller;

import com.careHive.dtos.User.UserProfileResponseDTO;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ResponseModel<Page<UserProfileResponseDTO>>> getUsers(
            @RequestParam(required = false) StatusEnum status,
            @RequestParam(required = false) RoleEnum role,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Pageable pageable
    ) throws CarehiveException {

        Page<UserProfileResponseDTO> users =
                userService.findAll(status, role, search, sortBy, sortDir, pageable);

        return ApiResponse.respond(
                users,
                "Users fetched successfully",
                "Unable to fetch users"
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseModel<String>> deleteUser(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CarehiveException {

        userService.deleteUserByID(authorizationHeader);

        return ApiResponse.respond(
                null,
                "User deleted successfully",
                "Failed to delete user"
        );
    }
}
