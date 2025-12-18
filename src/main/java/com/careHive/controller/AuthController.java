package com.careHive.controller;

import com.careHive.dtos.Auth.*;
import com.careHive.dtos.Otp.OtpRequestDTO;
import com.careHive.dtos.Password.PasswordResetConfirmDTO;
import com.careHive.dtos.Password.PasswordResetRequestDTO;
import com.careHive.dtos.Password.ChangePasswordDTO;
import com.careHive.dtos.User.UserProfileDTO;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // --------------------------------------------------------
    // 🔹 USER REGISTRATION & VERIFICATION
    // --------------------------------------------------------

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ResponseModel<AuthResponseDTO>> register(@RequestBody AuthRegisterDTO registerDTO)
            throws CarehiveException {
        AuthResponseDTO response = authService.register(registerDTO);
        return ApiResponse.respond(response, "User registered successfully", "User registration failed");
    }

    @Operation(summary = "Verify OTP sent to user email")
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseModel<String>> verifyOtp(@RequestBody OtpRequestDTO otpRequestDTO)
            throws CarehiveException {
        String message = authService.verifyOtp(otpRequestDTO);
        return ApiResponse.respond(message, "OTP verified successfully", "Invalid or expired OTP");
    }

    @Operation(summary = "Resend OTP to user email")
    @PostMapping("/resend-otp")
    public ResponseEntity<ResponseModel<String>> resendOtp(@RequestBody OtpRequestDTO otpRequestDTO)
            throws CarehiveException {
        String message = authService.resendOtp(otpRequestDTO.getEmail());
        return ApiResponse.respond(message, "OTP resent successfully", "Failed to resend OTP");
    }

    // --------------------------------------------------------
    // 🔹 LOGIN & OTP (for phone login)
    // --------------------------------------------------------

    @Operation(summary = "Send OTP for phone login")
    @PostMapping("/send-otp")
    public ResponseEntity<ResponseModel<String>> sendLoginOtp(@RequestBody PhoneOtpRequestDTO request)
            throws CarehiveException {
        String message = authService.sendOtp(request);
        return ApiResponse.respond(message, "OTP sent successfully", "Failed to send OTP");
    }

    @Operation(summary = "Login user using email/username/password or phone+OTP")
    @PostMapping("/login")
    public ResponseEntity<ResponseModel<LoginResponseDTO>> login(@RequestBody AuthLoginDTO loginDTO)
            throws CarehiveException {
        LoginResponseDTO response = authService.login(loginDTO);
        return ApiResponse.respond(response, "Login successful", "Invalid credentials");
    }

    // --------------------------------------------------------
    // 🔹 PASSWORD MANAGEMENT
    // --------------------------------------------------------

    @Operation(summary = "Send password reset token to user email")
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseModel<String>> forgotPassword(@RequestBody PasswordResetRequestDTO requestDTO)
            throws CarehiveException {
        String message = authService.forgotPassword(requestDTO);
        return ApiResponse.respond(message, "Password reset email sent successfully", "Failed to send reset email");
    }

    @Operation(summary = "Validate password reset token before setting new password")
    @GetMapping("/validate-reset-token")
    public ResponseEntity<ResponseModel<String>> validateResetToken(@RequestParam String token)
            throws CarehiveException {
        String message = authService.validatePasswordResetToken(token);
        return ApiResponse.respond(message, "Token is valid", "Token is invalid or expired");
    }

    @Operation(summary = "Reset password using reset token")
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseModel<String>> resetPassword(@RequestBody PasswordResetConfirmDTO requestDTO)
            throws CarehiveException {
        String message = authService.resetPassword(requestDTO);
        return ApiResponse.respond(message, "Password reset successfully", "Failed to reset password");
    }

    @Operation(summary = "Change password (for logged-in user)")
    @PostMapping("/change-password")
    public ResponseEntity<ResponseModel<String>> changePassword(@RequestBody ChangePasswordDTO requestDTO)
            throws CarehiveException {
        String message = authService.changePassword(requestDTO);
        return ApiResponse.respond(message, "Password changed successfully", "Failed to change password");
    }

    // --------------------------------------------------------
    // 🔹 USER PROFILE
    // --------------------------------------------------------

    @GetMapping("/me")
    public ResponseEntity<ResponseModel<UserProfileDTO>> getCurrentUser(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CarehiveException {
        UserProfileDTO userProfile =
                authService.getCurrentUser(authorizationHeader);
        return ApiResponse.respond(
                userProfile,
                "User profile fetched successfully",
                "Failed to fetch profile"
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseModel<UserProfileDTO>> updateCurrentUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UpdateUserProfileDTO updateDTO
    ) throws CarehiveException {
        UserProfileDTO updatedProfile =
                authService.updateCurrentUser(authorizationHeader, updateDTO);
        return ApiResponse.respond(
                updatedProfile,
                "User profile updated successfully",
                "Failed to update user profile"
        );
    }
}
