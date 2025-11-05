package com.careHive.serviceImpl;

import com.careHive.dtos.Auth.*;
import com.careHive.dtos.Otp.OtpRequestDTO;
import com.careHive.dtos.Password.ChangePasswordDTO;
import com.careHive.dtos.Password.PasswordResetConfirmDTO;
import com.careHive.dtos.Password.PasswordResetRequestDTO;
import com.careHive.dtos.User.UserProfileDTO;
import com.careHive.entities.*;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.VerificationStatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.*;
import com.careHive.security.JwtUtil;
import com.careHive.services.AuthService;
import com.careHive.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final ServiceRepository serviceRepository;

    @Override
    public AuthResponseDTO register(AuthRegisterDTO registerDTO) throws CarehiveException {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_EMAIL, "User with same email already exists");
        } else if (userRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_PROFILE, "User with same username already exists");
        } else if (userRepository.findByPhoneNumber(registerDTO.getPhoneNumber()).isPresent()) {
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_PROFILE, "User with same phone number already exists");
        }

        Role role = roleRepository.findByEnumCode(registerDTO.getRoleCode())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Role not found"));

        User user = User.builder()
                .name(registerDTO.getName())
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .roleCode(registerDTO.getRoleCode())
                .phoneNumber(registerDTO.getPhoneNumber())
                .isVerified(false)
                .caretakerStatus(VerificationStatusEnum.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        String otp = generateOtp();
        otpRepository.deleteByEmail(user.getEmail());
        otpRepository.save(OtpStore.builder()
                .email(user.getEmail())
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build());

        emailService.sendOtpEmail(user.getEmail(), otp);

        return AuthResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roleName(role.getName())
                .createdAt(user.getCreatedAt())
                .isVerified(false)
                .message("User registered successfully. Please verify OTP sent to your email.")
                .build();
    }

    @Override
    public String sendOtp(PhoneOtpRequestDTO requestDTO) throws CarehiveException {
        User user = userRepository.findByPhoneNumber(requestDTO.getPhoneNumber())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        String otp = generateOtp();

        otpRepository.deleteByEmail(user.getEmail());
        otpRepository.save(OtpStore.builder()
                .email(user.getEmail())
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build());
        emailService.sendOtpEmail(user.getEmail(), otp);
        return "OTP sent successfully to registered phone number.";
    }

    @Override
    public String verifyOtp(OtpRequestDTO otpRequestDTO) throws CarehiveException {
        OtpStore otpStore = otpRepository.findByEmail(otpRequestDTO.getEmail())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "No OTP found for this email"));

        if (otpStore.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
            otpRepository.deleteByEmail(otpRequestDTO.getEmail());
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "OTP has expired. Please request a new one.");
        }

        if (!otpStore.getOtp().equals(otpRequestDTO.getOtp())) {
            throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid OTP entered.");
        }

        User user = userRepository.findByEmail(otpRequestDTO.getEmail())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        user.setVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        otpRepository.deleteByEmail(otpRequestDTO.getEmail());

        return "OTP verified successfully. You can now log in.";
    }

    @Override
    public String resendOtp(String email) throws CarehiveException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        String otp = generateOtp();
        otpRepository.deleteByEmail(email);
        otpRepository.save(OtpStore.builder()
                .email(email)
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build());

        emailService.sendOtpEmail(email, otp);
        return "A new OTP has been sent successfully.";
    }

    @Override
    public String forgotPassword(PasswordResetRequestDTO passwordResetRequestDTO) throws CarehiveException {
        String email = passwordResetRequestDTO.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found with this email"));

        String token = UUID.randomUUID().toString();
        passwordResetTokenRepository.deleteByEmail(email);
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .expiryTime(LocalDateTime.now().plusMinutes(30))
                .build();
        passwordResetTokenRepository.save(passwordResetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), token);
        return "Password reset token has been sent to your registered email address.";
    }

    @Override
    public String resetPassword(PasswordResetConfirmDTO dto) throws CarehiveException {
        Optional<PasswordResetToken> tokenEntity = passwordResetTokenRepository.findByToken(dto.getToken());
        if (tokenEntity.isEmpty() || tokenEntity.get().getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid or expired token.");
        }

        User user = userRepository.findByEmail(tokenEntity.get().getEmail())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        passwordResetTokenRepository.delete(tokenEntity.get());
        return "Password reset successfully.";
    }

    @Override
    public String validatePasswordResetToken(String token) throws CarehiveException {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token not found"));

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token has expired");
        }

        return "Token is valid.";
    }

    @Override
    public LoginResponseDTO login(AuthLoginDTO loginDTO) throws CarehiveException {
        User user = null;

        if (loginDTO.getEmail() != null && !loginDTO.getEmail().isEmpty()) {
            user = userRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found with this email"));
        } else if (loginDTO.getUsername() != null && !loginDTO.getUsername().isEmpty()) {
            user = userRepository.findByUsername(loginDTO.getUsername())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found with this username"));
        } else if (loginDTO.getPhoneNumber() != null && !loginDTO.getPhoneNumber().isEmpty()) {
            user = userRepository.findByPhoneNumber(loginDTO.getPhoneNumber())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found with this phone number"));

            if (loginDTO.getOtp() == null || loginDTO.getOtp().isEmpty()) {
                throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "OTP is required for phone login");
            }

            OtpStore otpEntity = otpRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "No OTP found for this phone number"));

            if (!otpEntity.getOtp().equals(loginDTO.getOtp()) || otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid or expired OTP");
            }

            if (!user.isVerified()) {
                user.setVerified(true);
                userRepository.save(user);
            }

            otpRepository.delete(otpEntity);
        } else {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Email, Username, or Phone number is required");
        }

        if (loginDTO.getPhoneNumber() == null || loginDTO.getPhoneNumber().isEmpty()) {
            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid password");
            }
        }

        if (!user.isVerified()) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Please verify your email or phone first");
        }

        String token = jwtUtil.generateAccessToken(user.getEmail());

        Role role = roleRepository.findByEnumCode(user.getRoleCode())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Role not found"));

        return LoginResponseDTO.builder()
                .message("Login successful")
                .email(user.getEmail())
                .role(role.getName())
                .token(token)
                .build();
    }

    @Override
    public String changePassword(ChangePasswordDTO dto) throws CarehiveException {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Old password is incorrect");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "New password and confirm password do not match");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "New password cannot be same as the old password");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return "Password changed successfully.";
    }

    @Override
    public UserProfileDTO getCurrentUser(String token) throws CarehiveException {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        Role role = roleRepository.findByEnumCode(user.getRoleCode())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Role not found"));

        // Fetch associated services safely
        List<Services> services = user.getServiceIds() == null ?
                Collections.emptyList() :
                user.getServiceIds().stream()
                        .map(id -> serviceRepository.findById(id).orElse(null))
                        .filter(Objects::nonNull)
                        .toList();

        return UserProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roleCode(user.getRoleCode())
                .roleName(role.getName())
                .username(user.getUsername())
                .isVerified(user.isVerified())
                .documents(user.getDocuments())
                .services(services)
                .caretakerStatus(user.getCaretakerStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
