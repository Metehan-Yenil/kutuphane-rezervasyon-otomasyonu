package com.kutuphanerezervasyon.kutuphane.controller;

import com.kutuphanerezervasyon.kutuphane.dto.LoginRequest;
import com.kutuphanerezervasyon.kutuphane.dto.RegisterRequest;
import com.kutuphanerezervasyon.kutuphane.dto.UserDTO;
import com.kutuphanerezervasyon.kutuphane.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Kimlik doğrulama işlemlerini sağlayan controller
 * Kayıt ve giriş endpoint'leri burada tanımlı
 * Örnek: POST /api/auth/register, POST /api/auth/login
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = userService.register(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequest request) {
        UserDTO user = userService.login(request);
        return ResponseEntity.ok(user);
    }
}
