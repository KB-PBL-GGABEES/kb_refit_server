package org.refit.spring.ceo.controller;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.dto.UserResponseDto;
import org.refit.spring.auth.entity.User;
import org.refit.spring.auth.service.UserService;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.EmailRequestDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;
import org.refit.spring.ceo.service.CeoService;
import org.refit.spring.receipt.dto.ReceiptResponseDto;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ceo")
public class CeoController {
    final CeoService ceoService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    // 경비 처리가 필요한 내역 조회
    @GetMapping("/undone")
    public ResponseEntity<List<CeoListDto>> getListUndone() {
        List<CeoListDto> list = ceoService.getListUndone();
        return ResponseEntity.ok(list);
    }

    // 경비 청구 항목 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<ReceiptDetailDto> getReceiptDetail(
            @RequestParam("id") Long receipted) {
        return ResponseEntity.ok(ceoService.getReceiptDetail(receipted));
    }

    // 경비 처리 완료 내역 조회
    @GetMapping("/done")
    public ResponseEntity<List<CeoListDto>> getListDone(
            @RequestParam(value = "period", defaultValue = "1")
            int period) {
        List<CeoListDto> list = ceoService.getListDone(period);
        return ResponseEntity.ok(list);
    }

    // 처리 완료된 항목 이메일 전송
    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody EmailRequestDto request) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }

        String username = jwtTokenProvider.getUsername(token);
        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // 처리 완료된 항목 개수
        int countDoneReceipt = ceoService.countDoneReceipt();

        // 처리 완료된 항목 이메일 전송
        ceoService.sendEmail(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "경비 처리 항목을 보냈습니다.",
                "경비 처리 수", countDoneReceipt));
    }


    // 영수 처리 승인

    // 영수 처리 반려

    // 한달 법카 금액 조회

    // 법카 내역 조회

    // 페이지네이션

}
