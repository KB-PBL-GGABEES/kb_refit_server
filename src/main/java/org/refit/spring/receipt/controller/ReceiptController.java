package org.refit.spring.receipt.controller;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.entity.User;
import org.refit.spring.auth.service.UserService;
import org.refit.spring.mapper.ReceiptMapper;
import org.refit.spring.receipt.dto.ReceiptListDto;
import org.refit.spring.receipt.dto.ReceiptRequestDto;
import org.refit.spring.receipt.dto.ReceiptResponseDto;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.service.ReceiptService;
import org.refit.spring.reward.entity.Reward;
import org.refit.spring.reward.service.RewardService;
import org.refit.spring.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
public class ReceiptController {
    private final JwtTokenProvider jwtTokenProvider;
    private final ReceiptService receiptService;
    private final RewardService rewardService;

    private final UserService userService;


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader, @RequestBody ReceiptRequestDto receiptRequestDto) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }

        String username = jwtTokenProvider.getUsername(token);
        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Receipt receipt = receiptService.create(receiptRequestDto, user.getUserId());
        Reward reward = rewardService.create(receipt.getTotalPrice(), user.getUserId());
        userService.updatePoint(user, user.getTotalCarbonPoint() + reward.getCarbonPoint(), user.getTotalStarPoint() + reward.getReward());
        ReceiptResponseDto dto = ReceiptResponseDto.from(receipt, user.getUserId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestHeader("Authorization") String authHeader, @RequestParam("receiptId") Long receiptId) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }

        String username = jwtTokenProvider.getUsername(token);
        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Receipt receipt = receiptService.refund(user.getUserId(), receiptId);
        ReceiptResponseDto dto = ReceiptResponseDto.from(receipt, user.getUserId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/list")
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

        ReceiptListDto dto = receiptService.getList(user.getUserId(), cursorId);

        return ResponseEntity.ok(dto);
    }


    @GetMapping("/get")
    public ResponseEntity<?> get(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Long cursorId,
            @RequestParam("receiptId") Long receiptId) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }

        String username = jwtTokenProvider.getUsername(token);
        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Receipt receipt = receiptService.get(cursorId, receiptId);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping("/monthlyReport")
    public ResponseEntity<?> getTotal(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }
        String username = jwtTokenProvider.getUsername(token);
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(receiptService.getTotal(user.getUserId()));
    }

}
