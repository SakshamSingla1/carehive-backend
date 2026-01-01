package com.careHive.services;

import com.careHive.dtos.Auth.*;
import com.careHive.dtos.Otp.OtpRequestDTO;
import com.careHive.dtos.Password.*;
import com.careHive.dtos.User.UserProfileRequestDTO;
import com.careHive.dtos.User.UserProfileResponseDTO;
import com.careHive.exceptions.CarehiveException;

public interface AuthService {
    AuthResponseDTO register(AuthRegisterDTO registerDTO) throws CarehiveException;

    String sendOtp(PhoneOtpRequestDTO requestDTO) throws CarehiveException;

    String verifyOtp(OtpRequestDTO otpRequestDTO) throws CarehiveException;

    String resendOtp(String email) throws CarehiveException;

    LoginResponseDTO login(AuthLoginDTO loginDTO) throws CarehiveException;

    String forgotPassword(PasswordResetRequestDTO passwordResetRequestDTO) throws CarehiveException;

    String validatePasswordResetToken(String token) throws CarehiveException;

    String resetPassword(PasswordResetConfirmDTO dto) throws CarehiveException;

    String changePassword(String authorizationHeader,ChangePasswordDTO dto) throws CarehiveException;

    UserProfileResponseDTO getCurrentUser(String authorizationHeader) throws CarehiveException;

    UserProfileResponseDTO updateCurrentUser(String authorizationHeader, UserProfileRequestDTO dto) throws CarehiveException;
}
