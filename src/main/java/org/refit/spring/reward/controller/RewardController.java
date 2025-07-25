package org.refit.spring.reward.controller;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.entity.User;
import org.refit.spring.auth.service.UserService;
import org.refit.spring.reward.dto.RewardListDto;
import org.refit.spring.reward.service.RewardService;
import org.refit.spring.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reward")
@RequiredArgsConstructor
public class RewardController {
    private final JwtTokenProvider jwtTokenProvider;
    private final RewardService rewardService;

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getList(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Long cursorId) {

        String token = authHeader.replace("Bearer ", "");

        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }
        String username = jwtTokenProvider.getUsername(token);
        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        RewardListDto dto = rewardService.getList(user.getUserId(), cursorId);

        return ResponseEntity.ok(dto);
    }

}
