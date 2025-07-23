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

        Receipt receipt = receiptService.create(receiptRequestDto);
        ReceiptResponseDto dto = ReceiptResponseDto.from(receipt, user.getUserId(), receiptRequestDto.getReward());
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

//    @GetMapping("/total")
//    public ResponseEntity<?> getTotal(@RequestHeader("Authorization") String authHeader) {
//        String token = authHeader.replace("Bearer ", "");
//
//        if (!jwtTokenProvider.validateAccessToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
//        }
//        return ResponseEntity.ok();
//
//    }

}
