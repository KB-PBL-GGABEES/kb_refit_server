package org.refit.spring.receipt.controller;

import lombok.RequiredArgsConstructor;
import org.refit.spring.mapper.ReceiptMapper;
import org.refit.spring.receipt.dto.ReceiptRequestDto;
import org.refit.spring.receipt.dto.ReceiptResponseDto;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.service.ReceiptService;
import org.refit.spring.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
public class ReceiptController {
    private final JwtTokenProvider jwtTokenProvider;
    private final ReceiptMapper receiptMapper;
    private final ReceiptService receiptService;

//    @GetMapping("")
//    public ResponseEntity<?> getList(@RequestHeader("Authorization") String authHeader) {
//        String token = authHeader.replace("Bearer ", "");
//
//        if (!jwtTokenProvider.validateAccessToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
//        }
//
//        ReceiptResponseDto dto = ReceiptResponseDto.from()
//        return ResponseEntity.ok();
//    }

    @PostMapping("create")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader, @RequestBody ReceiptRequestDto receiptRequestDto) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }

        Receipt receipt = receiptService.create(receiptRequestDto);
        ReceiptResponseDto dto = ReceiptResponseDto.from(receipt, 1L, receiptRequestDto.getReward());
        return ResponseEntity.ok(dto);
    }
}
