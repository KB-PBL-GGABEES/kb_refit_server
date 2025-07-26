package org.refit.spring.ceo.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.EmailRequestDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;
import org.refit.spring.ceo.service.CeoService;
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

    @ApiOperation(value = "경비 처리가 필요한 내역 조회", notes = "경비 처리가 필요한 내역을 최신순으로 다 가져옵니다.")
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingReceipts(
            @UserId Long userId) {
        List<CeoListDto> list = ceoService.getPendingReceipts(userId);
        int countPendingReceipts = ceoService.countPendingReceipts(userId);
        int countCompletedReceiptsThisMonth = ceoService.countCompletedReceiptsThisMonth(userId);

        return ResponseEntity.ok(Map.of(
                "경비 처리가 필요한 내역 개수", countPendingReceipts,
                "이번 달 경비 처리 완료 내역 개수", countCompletedReceiptsThisMonth,
                "pendingReceipts", list
        ));
    }

    @ApiOperation(value = "경비 청구 항목 상세 조회", notes = "경비 청구 항목의 상세 정보를 보여줍니다.")
    @GetMapping("/receiptDetail")
    public ResponseEntity<ReceiptDetailDto> getReceiptDetail(
            @RequestParam("id") Long receipted,
            @UserId Long userId) {

        return ResponseEntity.ok(ceoService.getReceiptDetail(receipted, userId));
    }

    @ApiOperation(value = "경비 처리 완료 내역 조회", notes = "경비 처리가 완료된(승인/반려) 내역을 20개씩 가져옵니다.")
    @GetMapping("/completed")
    public ResponseEntity<List<Object>> getCompletedReceipts(
            @RequestParam(value = "period", defaultValue = "1") int period,
            @RequestParam(required = false) String cursorDateTime,
            @UserId Long userId) {

        List<CeoListDto> list = ceoService.getCompletedReceipts(period, cursorDateTime, userId);

        String nextCursorDateTime = list.size() < 20 ? null :
                list.get(list.size() - 1).getReceiptDateTime().toString();

        List<Object> response = new ArrayList<>(list);

        Map<String, Object> cursorMap = new java.util.HashMap<>();
        cursorMap.put("cursorId", nextCursorDateTime);
        response.add(cursorMap);

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "처리 완료된 항목 이메일 전송", notes = "경비 처리가 완료된(승인/반려) 항목을 특정 이메일로 보냅니다.")
    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(
            @RequestBody EmailRequestDto request,
            @UserId Long userId) {

        int countCompletedReceiptsReceipt = ceoService.countCompletedReceipts(userId);

        ceoService.sendEmail(request.getEmail(), userId);
        return ResponseEntity.ok(Map.of(
                "message", "경비 처리 항목을 보냈습니다.",
                "경비 처리 수", countCompletedReceiptsReceipt));
    }

    @ApiOperation(value = "영수 처리 승인 및 반려", notes = "process_state를 승인(accepted) 또는 반려(rejected)로 반영(Update)합니다.")
    @PatchMapping("/receiptProcessing")
    public ResponseEntity<Map<String, Object>> receiptProcessing(
            @RequestBody Map<String, Object> requestBody,
            @UserId Long userId) {

        Long receiptProcessId = Long.valueOf(requestBody.get("receiptProcessId").toString());
        String progressState = requestBody.get("progressState").toString();

        String rejectedReason = (requestBody.get("rejectedReason") != null)
                ? requestBody.get("rejectedReason").toString()
                : null;

        ceoService.receiptProcessing(receiptProcessId, progressState, rejectedReason, userId);

        return ResponseEntity.ok(Map.of(
                "message", "영수 처리 완료",
                "processStatus", progressState
        ));
    }

    // 한달 법카 금액 조회

    // 법카 내역 조회
}
