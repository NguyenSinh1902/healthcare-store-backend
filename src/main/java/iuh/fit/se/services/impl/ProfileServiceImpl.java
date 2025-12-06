package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.profile.UserProfileResponse;
import iuh.fit.se.dtos.profile.UserProfileUpdateRequest;
import iuh.fit.se.entities.auth.User;
import iuh.fit.se.mappers.UserProfileMapper;
import iuh.fit.se.repositories.UserRepository;
import iuh.fit.se.services.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserProfileMapper mapper;

    public ProfileServiceImpl(UserRepository userRepository, UserProfileMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return userRepository.findById(userId)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setAvatarUrl(req.getAvatarUrl());

        return mapper.toResponseDTO(userRepository.save(user));
    }

    // ADMIN
    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll()
                .stream().map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserProfileResponse updateUserStatus(Long userId, String status) {
        // TODO: Implement later
        return null;
    }

    @Override
    public void deleteUser(Long userId) {
        // TODO: Implement later
    }
}