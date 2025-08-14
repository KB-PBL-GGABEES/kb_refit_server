package org.refit.spring.ceo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.ceo.dto.*;
import org.refit.spring.ceo.dto.EmailSendDto;
import org.refit.spring.ceo.service.CeoService;
import org.refit.spring.ceo.service.ReceiptExportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

@Api(tags = "사장님 API", description = "영수 처리 및 법인카드 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ceo")
@Log4j
public class CeoController {
    final CeoService ceoService;
    final ReceiptExportService receiptExportService;

    @ApiOperation(value = "경비 처리가 필요한 내역 조회", notes = "경비 처리가 필요한 내역을 최신순으로 다 가져옵니다.")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingDetail(
            @ApiIgnore @UserId Long userId) {

        try {
            return ResponseEntity.ok(ceoService.getPendingDetail(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            log.error("경비 대기 내역 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "경비 대기 내역 조회 오류 발생"));
        }
    }

    @ApiOperation(value = "영수증 상세 내역 조회", notes = "영수증 상세 내역을 보여줍니다.")
    @GetMapping("/receipt/detail")
    public ResponseEntity<?> getReceiptList(
            @RequestParam Long receiptId) {

        try {
            return ResponseEntity.ok(ceoService.getReceiptList(receiptId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            log.error("영수증 상세 내역 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "영수증 상세 내역 조회 오류 발생"));
        }
    }

    @ApiOperation(value = "경비 처리 완료 내역 조회", notes = "경비 처리가 완료된(승인/반려) 내역을 가져옵니다.")
    @GetMapping("/completed")
    public ResponseEntity<?> getCompletedReceipts(
            @ApiIgnore @UserId Long userId,
            @ModelAttribute ReceiptFilterDto receiptFilterDto) {

        try {
            ReceiptListCursorDto dto = ceoService.getCompletedReceipts(userId, receiptFilterDto);
            System.out.println(dto);
            return ResponseEntity.ok(dto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            log.error("경비 처리 완료 내역 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "경비 처리 완료 내역 조회 오류 발생"));
        }
    }

    @ApiOperation(value = "처리 완료된 항목 개수 반환", notes = "이번 달 처리가 완료된 항목들의 개수를 반환합니다.")
    @GetMapping("/monthlySummary")
    public ResponseEntity<?> getMonthlySummary(
            @ApiIgnore @UserId Long userId) {

        try {
            return ResponseEntity.ok(ceoService.monthlySummary(userId));
        } catch (Exception e) {
            log.error("이번 달 처리가 완료된 항목들의 개수 반환 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "이번 달 처리가 완료된 항목들의 개수 반환 오류 발생"));
        }
    }

    @ApiOperation(value = "처리 완료된 항목 이메일 전송", notes = "경비 처리가 완료된(승인/반려) 항목을 특정 이메일로 보냅니다.")
    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(
            @RequestBody EmailSendDto request, @ApiIgnore @UserId Long userId) {

        //csv파일로 만들기
        try {
            receiptExportService.generateAndSendCsvByEmail(userId, request.getEmail(), request.getStartDate(), request.getEndDate());
            return ResponseEntity.ok("이메일 전송 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이메일 전송 실패: " + e.getMessage());
        }
    }

    @GetMapping("/acceptedCount")
    @ApiOperation(value = "경비 처리 승인 항목 개수", notes = "주어진 기간 동안의 경비 처리 승인 항목의 개수를 세어 반환합니다.")
    public ResponseEntity<?> countAccepted(@ApiIgnore @UserId Long userId,
                                           @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                           @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            return ResponseEntity.ok(ceoService.acceptedSummary(userId, startDate, endDate));
        } catch (Exception e) {
            log.error("경비 처리 승인 항목 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "경비 처리 승인 항목 개수 오류 발생"));
        }
    }

    @PatchMapping("/receiptProcessing")
    @ApiOperation(value = "영수 처리 승인 및 반려 / 법카 영수 반려", notes = "process_state 를 승인(accepted) 또는 반려(rejected)로 반영합니다.")
    public ResponseEntity<?> receiptProcess(
            @RequestBody ReceiptProcessingRequestDto request
    ) {
        try{
            ReceiptProcessDto result = ceoService.receiptProcessing(
                    request.getReceiptId(),
                    request.getProgressState(),
                    request.getRejectedReason()
            );
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            log.error("영수 처리 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "영수 처리 오류 발생"));
        }
    }

    @ApiOperation(value = "한달 법카 금액 조회", notes = "법카의 이번 달 사용액과 지난달 사용액을 가져옵니다.")
    @GetMapping("/corporateCardCost")
    public ResponseEntity<?> getCorporateCardTotalPrice(
            @ApiIgnore @UserId Long userId) {

        try {
            return ResponseEntity.ok(ceoService.getCorporateCardTotalPrice(userId));
        } catch (Exception e) {
            log.error("한달 법카 금액 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "한달 법카 금액 조회 오류 발생"));
        }
    }

    @ApiOperation(value = "법카 내역 조회", notes = "법카의 사용 내역을 보여줍니다.")
    @GetMapping("/corporateCard")
    public ResponseEntity<?> getCorporateCard(
            @ApiIgnore @UserId Long userId,
            @ModelAttribute ReceiptFilterDto receiptFilterDto) {

        try {
            CorporateCardListCursorDto dto = ceoService.getCorporateCardReceipts(userId, receiptFilterDto);
            return ResponseEntity.ok(dto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            log.error("법카 내역 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "법카 내역 조회 오류 발생"));
        }
    }
}
