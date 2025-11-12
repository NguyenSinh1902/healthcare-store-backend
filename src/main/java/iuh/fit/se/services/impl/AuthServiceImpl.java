package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.auth.*;
import iuh.fit.se.entities.auth.Role;
import iuh.fit.se.entities.auth.User;
import iuh.fit.se.exceptions.BadRequestException;
import iuh.fit.se.exceptions.ValidationException;
import iuh.fit.se.repositories.UserRepository;
import iuh.fit.se.utils.JwtUtil;
import iuh.fit.se.services.AuthService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    //register
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        //Kiem tra trung email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists in the system");
        }

        //Kiem tra xac nhan mat khau
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            List<Map<String, String>> errors = List.of(
                    Map.of("field", "confirmPassword", "message", "Password confirmation does not match")
            );
            throw new ValidationException(errors);
        }

        //Ma hoa mat khau bang BCrypt
        String hashed = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        //Tao user moi
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(hashed);
        user.setRole(request.getRole() == null ? Role.USER : request.getRole());
        userRepository.save(user);

        //Tra ket qua
        RegisterResponse response = new RegisterResponse();
        response.setSuccess(true);
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setMessage("User registered successfully");

        return response;
    }

    //Dang nhap
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        //Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        //Check password
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        //Check account status
        switch (user.getStatus()) {
            case INACTIVE -> throw new BadRequestException("Your account is temporarily inactive.");
            case BANNED -> throw new BadRequestException("Your account has been banned.");
            default -> {
            }
        }

        //Generate JWT token
        String token = jwtUtil.generateToken(user);

        //Return login result
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setToken(token);
        response.setRole(user.getRole());
        response.setMessage("Login successful");

        return response;
    }

}
