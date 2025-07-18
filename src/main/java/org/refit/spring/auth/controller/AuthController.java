package org.refit.spring.auth.controller;


import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.dto.LoginRequestDto;
import org.refit.spring.auth.dto.UserResponseDto;
import org.refit.spring.auth.entity.User;
import org.refit.spring.auth.service.UserService;
import org.refit.spring.mapper.UserMapper;
import org.refit.spring.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final UserService userService;

    //security에서 제공하는 login 사용할 예정
    @GetMapping("/test")
    public String test() {
        return new BCryptPasswordEncoder().encode("1234");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyData(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }

        String username = jwtTokenProvider.getUsername(token);
        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        UserResponseDto dto = UserResponseDto.from(user);
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token invalid or expired");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        String role = getRoleFromDB(username);

        String newAccessToken = jwtTokenProvider.createToken(username, role);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    private String getRoleFromDB(String username) {
        return userMapper.findByUsername(username).getRole().name();
    }
}
