package iuh.fit.se.controllers;

import iuh.fit.se.dtos.auth.LoginRequest;
import iuh.fit.se.dtos.auth.LoginResponse;
import iuh.fit.se.dtos.auth.RegisterRequest;
import iuh.fit.se.dtos.auth.RegisterResponse;
import iuh.fit.se.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
