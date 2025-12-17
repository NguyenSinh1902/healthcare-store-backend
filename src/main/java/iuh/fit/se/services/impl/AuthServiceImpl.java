package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.auth.*;
import iuh.fit.se.entities.auth.Role;
import iuh.fit.se.entities.auth.User;
import iuh.fit.se.entities.auth.UserStatus;
import iuh.fit.se.exceptions.BadRequestException;
import iuh.fit.se.exceptions.ValidationException;
import iuh.fit.se.repositories.UserRepository;
import iuh.fit.se.services.AuthService;
import iuh.fit.se.services.EmailService;
import iuh.fit.se.utils.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final EmailService emailService; // Thêm EmailService

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        //kiem tra xem user da ton tai chua
        Optional<User> existingUserOpt = userRepository.findByEmail(request.getEmail());

        User user;

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            //tai khoan ACTIVE hoac BANNED
            if (existingUser.getStatus() == UserStatus.ACTIVE || existingUser.getStatus() == UserStatus.BANNED) {
                throw new BadRequestException("Email already exists in the system");
            }

            //tai khoan INACTIVE (Can phan loai)
            if (existingUser.getStatus() == UserStatus.INACTIVE) {

                // neu Code là NULL (hoac rong) -> Tuc là Admin da update status(inactive) -> chan
                if (existingUser.getVerificationCode() == null || existingUser.getVerificationCode().trim().isEmpty()) {
                    throw new BadRequestException("Your account has been locked by Administrator. Please contact support.");
                }

                // Neu Code khac NULL -> dang dang ky -> cho phep dang ky tiep (Reset Code)
            }

            //tai su dung user (Cho truong hop quen verify)
            user = existingUser;
            user.setFullName(request.getFullName());
            String hashed = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
            user.setPassword(hashed);
            // user.setRole(...) // khong doi role neu user da ton tai

        } else {
            //tao moi user
            user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            String hashed = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
            user.setPassword(hashed);
            user.setRole(request.getRole() == null ? Role.USER : request.getRole());
            user.setStatus(UserStatus.INACTIVE);
        }

        //xu ly chung verification(tao code moi, thoi gian het han)
        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setVerificationCode(code);
        user.setVerificationExpiration(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user); // Save de len user cũ hoac insert user moi

        emailService.sendVerificationEmail(user.getEmail(), code);

        RegisterResponse response = new RegisterResponse();
        response.setSuccess(true);
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setMessage("Registration successful. Please check your email to verify account.");

        return response;
    }

    //Verify
    public void verifyAccount(VerifyRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        //kiem tra tk da active chua
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new BadRequestException("Account is already verified");
        }

        //Kiem tra ma code het han chua
        if (user.getVerificationExpiration() == null ||
                user.getVerificationExpiration().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification code has expired");
        }

        //Kiem tra ma code co dung hay ko
        if (user.getVerificationCode() == null ||
                !user.getVerificationCode().equals(request.getVerificationCode())) {
            throw new BadRequestException("Invalid verification code");
        }

        //Kich hoat tk
        user.setStatus(UserStatus.ACTIVE);
        user.setVerificationCode(null);
        user.setVerificationExpiration(null);

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        switch (user.getStatus()) {
            case INACTIVE -> {
                // neu co ma xac thuc -> Tuc la tai khoan moi, chua verify
                if (user.getVerificationCode() != null) {
                    throw new BadRequestException("Your account is not verified. Please check email.");
                }
                // neu khong co ma (null) -> Tuc là Admin da update status(inactive) -> chan
                else {
                    throw new BadRequestException("Your account has been locked by Administrator. Please contact support.");
                }
            }
            case BANNED -> throw new BadRequestException("Your account has been banned for violating policies.");
            default -> {}
        }

        String token = jwtUtil.generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setToken(token);
        response.setRole(user.getRole());
        response.setMessage("Login successful");

        return response;
    }
}