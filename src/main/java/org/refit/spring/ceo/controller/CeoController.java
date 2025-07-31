package org.refit.spring.ceo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.CorporateCardListDto;
import org.refit.spring.ceo.dto.EmailRequestDto;
import org.refit.spring.ceo.dto.ReceiptListDto;
import org.refit.spring.ceo.service.CeoService;
import org.refit.spring.common.pagination.CursorPageRequest;
import org.refit.spring.common.pagination.CursorPageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@Api(tags = "사장님 API", description = "영수 처리 및 법인카드 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ceo")
public class CeoController {
    final CeoService ceoService;

    @ApiOperation(value = "경비 처리가 필요한 내역 조회", notes = "경비 처리가 필요한 내역을 최신순으로 다 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "경비 처리가 필요한 내역 조회 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingReceipts(
            @ApiIgnore @UserId Long userId) {
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
    @GetMapping("/receiptList")
    public ResponseEntity<ReceiptListDto> getReceiptList(
            @RequestParam("userId") Long receipted,
            @ApiIgnore @UserId Long userId) {

        return ResponseEntity.ok(ceoService.getReceiptList(receipted, userId));
    }

    @ApiOperation(value = "경비 처리 완료 내역 조회", notes = "경비 처리가 완료된(승인/반려) 내역을 20개씩 가져옵니다.")
    @GetMapping("/completed")
    public ResponseEntity<?> getCompletedReceipts(
            @RequestParam(value = "period", defaultValue = "1") int period,
            @RequestParam(required = false) Long cursorId,
            @ApiIgnore @UserId Long userId) {

        CeoListDto dto = ceoService.getCompletedReceipts(period, cursorId, userId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "처리 완료된 항목 이메일 전송", notes = "경비 처리가 완료된(승인/반려) 항목을 특정 이메일로 보냅니다.")
    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(
            @RequestBody EmailRequestDto request,
            @ApiIgnore @UserId Long userId) {

        int countCompletedReceiptsReceipt = ceoService.countCompletedReceipts(userId);

        ceoService.sendEmail(request.getEmail(), userId);
        return ResponseEntity.ok(Map.of(
                "message", "경비 처리 항목을 보냈습니다.",
                "경비 처리 수", countCompletedReceiptsReceipt));
    }

    @ApiOperation(value = "영수 처리 승인 및 반려 / 법카 영수 반려", notes = "process_state 를 승인(accepted) 또는 반려(rejected)로 반영(Update)합니다.")
    @PatchMapping("/receiptProcessing")
    public ResponseEntity<Map<String, Object>> receiptProcessing(
            @RequestBody Map<String, Object> requestBody,
            @ApiIgnore @UserId Long userId) {

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

    @ApiOperation(value = "한달 법카 금액 조회", notes = "법카의 이번 달 사용액과 지난달 사용액을 가져옵니다.")
    @GetMapping("/corporateCardCost")
    public ResponseEntity<Map<String, Object>> getCorporateCardCost(
            @ApiIgnore @UserId Long userId) {
        return ResponseEntity.ok(ceoService.getCorporateCardCost(userId));
    }

    @ApiOperation(value = "법카 내역 조회", notes = "법카의 사용 내역을 보여줍니다.")
    @GetMapping("/corporateCard")
    public ResponseEntity<CursorPageResponse<CorporateCardListDto>> getCorporateCard(
            @ModelAttribute CursorPageRequest pageRequest,
            @ApiIgnore @UserId Long userId) {

        List<CorporateCardListDto> list = ceoService.getCorporateCardReceipts(pageRequest, userId);

        Long nextCursor = (list.size() < pageRequest.getSize())
                ? null
                : list.get(list.size() - 1).getReceiptId();

        return ResponseEntity.ok(CursorPageResponse.of(
                list, pageRequest.getSize(), "cursor", nextCursor
        ));
    }
}
