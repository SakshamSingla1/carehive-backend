package com.careHive.serviceImpl;

import com.careHive.dtos.Auth.*;
import com.careHive.dtos.Otp.OtpRequestDTO;
import com.careHive.dtos.Password.ChangePasswordDTO;
import com.careHive.dtos.Password.PasswordResetConfirmDTO;
import com.careHive.dtos.Password.PasswordResetRequestDTO;
import com.careHive.dtos.Auth.UpdateUserProfileDTO;
import com.careHive.dtos.User.UserProfileDTO;
import com.careHive.entities.*;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.VerificationStatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.*;
import com.careHive.security.JwtUtil;
import com.careHive.services.AuthService;
import com.careHive.services.NTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final NTService ntService;
    private final ColorThemeRepository colorThemeRepository;
    private final NavLinkRepository navLinkRepository;

    @Override
    public AuthResponseDTO register(AuthRegisterDTO registerDTO) throws CarehiveException {

        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent())
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_EMAIL, "User with same email already exists");

        if (userRepository.findByUsername(registerDTO.getUsername()).isPresent())
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_PROFILE, "User with same username already exists");

        if (userRepository.findByPhone(registerDTO.getPhone()).isPresent())
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_PROFILE, "User with same phone number already exists");

        Role role = roleRepository.findByEnumCode(registerDTO.getRoleCode().name())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Role not found"));

        Users user = Users.builder()
                .name(registerDTO.getName())
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .roleCode(registerDTO.getRoleCode())
                .phone(registerDTO.getPhone())
                .emailVerified(VerificationStatusEnum.PENDING)
                .phoneVerified(VerificationStatusEnum.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String rawOtp = generateRawOtp();
        String encodedOtp = passwordEncoder.encode(rawOtp);

        otpRepository.deleteByEmail(user.getEmail());
        otpRepository.save(
                OtpStore.builder()
                        .email(user.getEmail())
                        .otp(encodedOtp)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusMinutes(10))
                        .build()
        );

        ntService.sendNotification(
                "OTP-VERIFICATION",
                Map.of("name", user.getName(), "otp", rawOtp),
                user.getEmail()
        );

        return AuthResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roleName(role.getName())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .createdAt(user.getCreatedAt())
                .message("User registered successfully. Please verify OTP sent to your email.")
                .build();
    }

    @Override
    public String sendOtp(PhoneOtpRequestDTO dto) throws CarehiveException {

        Users user = userRepository.findByPhone(dto.getPhone())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        String rawOtp = generateRawOtp();
        String encodedOtp = passwordEncoder.encode(rawOtp);

        otpRepository.deleteByEmail(user.getEmail());
        otpRepository.save(
                OtpStore.builder()
                        .email(user.getEmail())
                        .otp(encodedOtp)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusMinutes(10))
                        .build()
        );

        ntService.sendNotification(
                "OTP-VERIFICATION",
                Map.of("name", user.getName(), "otp", rawOtp),
                user.getEmail()
        );

        return "OTP sent successfully";
    }

    @Override
    public String verifyOtp(OtpRequestDTO dto) throws CarehiveException {

        OtpStore otpStore = otpRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "OTP not found"));

        if (otpStore.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepository.deleteByEmail(dto.getEmail());
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "OTP expired");
        }

        if (!passwordEncoder.matches(dto.getOtp(), otpStore.getOtp())) {
            throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid OTP");
        }

        Users user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        user.setEmailVerified(VerificationStatusEnum.VERIFIED);
        user.setPhoneVerified(VerificationStatusEnum.VERIFIED);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        otpRepository.deleteByEmail(dto.getEmail());

        return "OTP verified successfully";
    }

    @Override
    public String resendOtp(String email) throws CarehiveException {

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        String rawOtp = generateRawOtp();
        String encodedOtp = passwordEncoder.encode(rawOtp);

        otpRepository.deleteByEmail(email);
        otpRepository.save(
                OtpStore.builder()
                        .email(email)
                        .otp(encodedOtp)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusMinutes(10))
                        .build()
        );

        ntService.sendNotification(
                "OTP-VERIFICATION",
                Map.of("name", user.getName(), "otp", rawOtp),
                user.getEmail()
        );

        return "OTP resent successfully";
    }

    @Override
    public String forgotPassword(PasswordResetRequestDTO dto) throws CarehiveException {

        Users user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        String token = UUID.randomUUID().toString();

        passwordResetTokenRepository.deleteByEmail(dto.getEmail());
        passwordResetTokenRepository.save(
                PasswordResetToken.builder()
                        .email(dto.getEmail())
                        .token(token)
                        .expiryTime(LocalDateTime.now().plusMinutes(30))
                        .build()
        );

        ntService.sendNotification(
                "FORGOT-PASSWORD-TOKEN",
                Map.of("name", user.getName(), "resetLink", frontendUrl + "?token=" + token),
                user.getEmail()
        );

        return "Password reset link sent";
    }

    @Override
    public String validatePasswordResetToken(String token) throws CarehiveException {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token not found"));

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token expired");

        return "Token is valid";
    }

    @Override
    public String resetPassword(PasswordResetConfirmDTO dto) throws CarehiveException {

        PasswordResetToken token = passwordResetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid token"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token expired");

        Users user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword()))
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "New password must be different from old password");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        passwordResetTokenRepository.delete(token);

        return "Password reset successful";
    }

    @Override
    public LoginResponseDTO login(AuthLoginDTO dto) throws CarehiveException {

        Users user;

        if (dto.getEmail() != null) {
            user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        } else if (dto.getUsername() != null) {
            user = userRepository.findByUsername(dto.getUsername())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        } else {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Invalid login request");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid password");

        if (user.getEmailVerified() != VerificationStatusEnum.VERIFIED || user.getPhoneVerified() != VerificationStatusEnum.VERIFIED)
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Account not verified");

        String token = jwtUtil.generateAccessToken(user.getEmail());

        Role role = roleRepository.findByEnumCode(user.getRoleCode().name())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Role not found"));

        List<ColorTheme> themes = colorThemeRepository.findByRole(user.getRoleCode());
        ColorTheme defaultTheme = themes.stream()
                .filter(t -> "default".equalsIgnoreCase(t.getThemeName()))
                .findFirst()
                .orElse(null);

        return LoginResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(role.getName())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .token("Bearer " + token)
                .themes(themes)
                .defaultTheme(defaultTheme)
                .navLinks(navLinkRepository.findByRoleCode(user.getRoleCode()))
                .build();
    }

    @Override
    public String changePassword(String authorizationHeader, ChangePasswordDTO dto) throws CarehiveException {

        String email = extractEmailFromHeader(authorizationHeader);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
            throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Incorrect current password");

        if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Passwords do not match");

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword()))
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "New password must be different from old password");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "Password changed successfully";
    }

    @Override
    public UserProfileDTO getCurrentUser(String authorizationHeader) throws CarehiveException {

        String email = extractEmailFromHeader(authorizationHeader);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        Role role = roleRepository.findByEnumCode(user.getRoleCode().name())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Role not found"));

        return UserProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .username(user.getUsername())
                .roleCode(user.getRoleCode())
                .roleName(role.getName())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public UserProfileDTO updateCurrentUser(String authorizationHeader, UpdateUserProfileDTO dto)
            throws CarehiveException {

        String email = extractEmailFromHeader(authorizationHeader);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return getCurrentUser(authorizationHeader);
    }

    private String generateRawOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private String extractEmailFromHeader(String header) throws CarehiveException {
        if (header == null || !header.startsWith("Bearer "))
            throw new CarehiveException(ExceptionCodeEnum.UNAUTHORIZED, "Invalid authorization header");
        return jwtUtil.extractEmail(header.substring(7));
    }
}
