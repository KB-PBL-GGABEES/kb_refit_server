package org.refit.spring.ceo.controller;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.entity.User;
import org.refit.spring.auth.service.UserService;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.EmailRequestDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;
import org.refit.spring.ceo.service.CeoService;
import org.refit.spring.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public ResponseEntity<List<Object>> getListUndone(
            @RequestParam(required = false) Long cursorId) {

        List<CeoListDto> list = ceoService.getListUndone(cursorId);
        Long nextCursorId = list.size() < 20 ? null : list.get(list.size() - 1).getReceiptId();

        List<Object> response = new ArrayList<>(list);

        Map<String, Object> cursorMap = new java.util.HashMap<>();
        cursorMap.put("cursorId", nextCursorId);
        response.add(cursorMap);

        return ResponseEntity.ok(response);
    }

    // 경비 청구 항목 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<ReceiptDetailDto> getReceiptDetail(
            @RequestParam("id") Long receipted) {
        return ResponseEntity.ok(ceoService.getReceiptDetail(receipted));
    }

    // 경비 처리 완료 내역 조회
    @GetMapping("/done")
    public ResponseEntity<List<Object>> getListDone(
            @RequestParam(value = "period", defaultValue = "1") int period,
            @RequestParam(required = false) String cursorDateTime) {
        List<CeoListDto> list = ceoService.getListDone(period, cursorDateTime);

        String nextCursorDateTime = list.size() < 20 ? null :
                list.get(list.size() - 1).getReceiptDateTime().toString();

        List<Object> response = new ArrayList<>(list);

        Map<String, Object> cursorMap = new java.util.HashMap<>();
        cursorMap.put("cursorId", nextCursorDateTime);
        response.add(cursorMap);

        return ResponseEntity.ok(response);
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
}
