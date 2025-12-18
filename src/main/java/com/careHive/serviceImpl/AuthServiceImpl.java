package com.careHive.serviceImpl;

import com.careHive.dtos.Auth.*;
import com.careHive.dtos.Otp.OtpRequestDTO;
import com.careHive.dtos.Password.ChangePasswordDTO;
import com.careHive.dtos.Password.PasswordResetConfirmDTO;
import com.careHive.dtos.Password.PasswordResetRequestDTO;
import com.careHive.dtos.User.UserProfileDTO;
import com.careHive.entities.*;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.VerificationStatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.*;
import com.careHive.security.JwtUtil;
import com.careHive.services.AuthService;
import com.careHive.services.EmailService;
import com.careHive.services.NTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final ServiceRepository serviceRepository;
    private final NTService ntService;
    private final ColorThemeRepository colorThemeRepository;
    private final NavLinkRepository navLinkRepository;

    @Override
    public AuthResponseDTO register(AuthRegisterDTO registerDTO) throws CarehiveException {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_EMAIL, "User with same email already exists");
        } else if (userRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_PROFILE, "User with same username already exists");
        } else if (userRepository.findByPhoneNumber(registerDTO.getPhoneNumber()).isPresent()) {
            throw new CarehiveException(ExceptionCodeEnum.DUPLICATE_PROFILE, "User with same phone number already exists");
        }

        Role role = roleRepository.findByEnumCode(registerDTO.getRoleCode().name())
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

        Map<String, Object> variables = Map.of(
                "name", user.getName(),
                "otp", otp
        );
        ntService.sendNotification("OTP-VERIFICATION", variables, user.getEmail());

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
        Map<String, Object> variables = Map.of(
                "name", user.getName(),
                "otp", otp
        );
        ntService.sendNotification("OTP-VERIFICATION", variables, user.getEmail());
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

        Map<String, Object> variables = Map.of(
                "name", user.getName(),
                "otp", otp
        );
        ntService.sendNotification("OTP-VERIFICATION", variables, user.getEmail());
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

        String resetLink = frontendUrl + "?token=" + token;  // <-- Value available here
        Map<String, Object> variables = Map.of(
                "name", user.getName(),
                "resetLink", resetLink
        );
        ntService.sendNotification("FORGOT-PASSWORD-TOKEN", variables, user.getEmail());
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

        // ---------------- FIND USER ----------------
        if (loginDTO.getEmail() != null && !loginDTO.getEmail().isEmpty()) {
            user = userRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND,
                            "User not found with this email"));
        } else if (loginDTO.getUsername() != null && !loginDTO.getUsername().isEmpty()) {
            user = userRepository.findByUsername(loginDTO.getUsername())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND,
                            "User not found with this username"));
        } else if (loginDTO.getPhoneNumber() != null && !loginDTO.getPhoneNumber().isEmpty()) {

            user = userRepository.findByPhoneNumber(loginDTO.getPhoneNumber())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND,
                            "User not found with this phone number"));

            if (loginDTO.getOtp() == null || loginDTO.getOtp().isEmpty()) {
                throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "OTP is required for phone login");
            }

            OtpStore otp = otpRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST,
                            "No OTP found for this phone number"));

            if (!otp.getOtp().equals(loginDTO.getOtp())
                    || otp.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS,
                        "Invalid or expired OTP");
            }

            if (!user.isVerified()) {
                user.setVerified(true);
                userRepository.save(user);
            }

            otpRepository.delete(otp);
        } else {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST,
                    "Email, Username, or Phone number is required");
        }

        // ---------------- PASSWORD ----------------
        if (loginDTO.getPhoneNumber() == null && !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid password");
        }

        if (!user.isVerified()) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST,
                    "Please verify your email or phone first");
        }

        // ---------------- JWT TOKEN ----------------
        String token = jwtUtil.generateAccessToken(user.getEmail());

        Role role = roleRepository.findByEnumCode(user.getRoleCode().name())
                .orElseThrow(() ->
                        new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Role not found")
                );

        // ---------------- FETCH ALL THEMES ----------------
        List<ColorTheme> themes = colorThemeRepository.findByRole(user.getRoleCode());

        // ---------------- DEFAULT BACKUP THEME ----------------
        ColorTheme defaultTheme = themes.stream()
                .filter(t -> "default".equalsIgnoreCase(t.getThemeName()))
                .findFirst()
                .orElse(themes.isEmpty() ? null : themes.get(0));

        List<NavLink> navLinks = navLinkRepository.findByRoleCode(user.getRoleCode());

        // ---------------- FINAL RESPONSE ----------------
        return LoginResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .phone(user.getPhoneNumber())
                .email(user.getEmail())
                .role(role.getName())
                .token(token)
                .themes(themes)          // ⚡ FRONTEND GETS ALL THEMES
                .defaultTheme(defaultTheme) // ⚡ Identify the active one
                .navLinks(navLinks)
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
    public UserProfileDTO getCurrentUser(String authorizationHeader)
            throws CarehiveException {

        String email = extractEmailFromHeader(authorizationHeader);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new CarehiveException(
                                ExceptionCodeEnum.PROFILE_NOT_FOUND,
                                "User not found"
                        )
                );

        Role role = roleRepository.findByEnumCode(user.getRoleCode().name())
                .orElseThrow(() ->
                        new CarehiveException(
                                ExceptionCodeEnum.BAD_REQUEST,
                                "Role not found"
                        )
                );

        List<Services> services =
                Optional.ofNullable(user.getServiceIds())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(id -> serviceRepository.findById(id).orElse(null))
                        .filter(Objects::nonNull)
                        .toList();

        return UserProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .username(user.getUsername())
                .roleCode(user.getRoleCode().name())
                .roleName(role.getName())
                .services(services)
                .documents(user.getDocuments())
                .caretakerStatus(user.getCaretakerStatus())
                .isVerified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // ------------------------------------------------------------------------
    // UPDATE CURRENT USER
    // ------------------------------------------------------------------------
    @Override
    public UserProfileDTO updateCurrentUser(
            String authorizationHeader,
            UpdateUserProfileDTO updateDTO
    ) throws CarehiveException {

        String email = extractEmailFromHeader(authorizationHeader);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new CarehiveException(
                                ExceptionCodeEnum.PROFILE_NOT_FOUND,
                                "User not found"
                        )
                );

        if (updateDTO.getName() != null) {
            user.setName(updateDTO.getName());
        }

        if (updateDTO.getCaretakerStatus() != null) {
            user.setCaretakerStatus(updateDTO.getCaretakerStatus());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return getCurrentUser(authorizationHeader);
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private String extractEmailFromHeader(String authorizationHeader)
            throws CarehiveException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CarehiveException(
                    ExceptionCodeEnum.UNAUTHORIZED,
                    "Missing or invalid Authorization header"
            );
        }
        return jwtUtil.extractEmail(authorizationHeader.substring(7));
    }

}
