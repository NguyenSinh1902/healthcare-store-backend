package iuh.fit.se.services;

import iuh.fit.se.dtos.profile.UserProfileResponse;
import iuh.fit.se.dtos.profile.UserProfileUpdateRequest;

import java.util.List;

public interface ProfileService {
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request);

    //cho ADMIN
    List<UserProfileResponse> getAllUsers();
    UserProfileResponse updateUserStatus(Long userId, String status);
    void deleteUser(Long userId);
}
