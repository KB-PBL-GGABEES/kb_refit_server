package org.refit.spring.auth.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.dto.LoginRequestDto;
import org.refit.spring.auth.dto.UserResponseDto;
import org.refit.spring.auth.entity.User;
import org.refit.spring.auth.service.UserService;
import org.refit.spring.mapper.UserMapper;
import org.refit.spring.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Api(tags = "권한 관련 API", description = "로그인, refresh token, mydata 관련 API입니다..")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final UserService userService;

    //security에서 제공하는 login 사용할 예정
    @GetMapping("/test")
    public List<String> test() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(new BCryptPasswordEncoder().encode("010101"));
        arrayList.add(new BCryptPasswordEncoder().encode("020202"));
        arrayList.add(new BCryptPasswordEncoder().encode("030303"));
        arrayList.add(new BCryptPasswordEncoder().encode("040404"));
        arrayList.add(new BCryptPasswordEncoder().encode("050505"));
        arrayList.add(new BCryptPasswordEncoder().encode("060606"));
        return arrayList;
    }

    @ApiOperation(value = "현재 로그인한 유저 정보 조회 API", notes = "현재 로그인한 유저의 정보를 조회할 수 있습니다.")
//    @GetMapping("/me")
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
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


    @ApiOperation(value = "새 access token 발급", notes = "refresh token을 이용하여 새로운 access token을 발급하는 API입니다.")
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
