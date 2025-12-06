package iuh.fit.se.controllers;

import iuh.fit.se.dtos.profile.UserProfileResponse;
import iuh.fit.se.dtos.profile.UserProfileUpdateRequest;
import iuh.fit.se.services.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // User updates profile
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfile(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserProfileUpdateRequest dto) {
        return ResponseEntity.ok(profileService.updateProfile(id, dto));
    }

    // ADMIN manages users
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<UserProfileResponse> users = profileService.getAllUsers();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "All user profiles retrieved successfully");
        body.put("data", users);

        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserProfileResponse> updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(profileService.updateUserStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        profileService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}