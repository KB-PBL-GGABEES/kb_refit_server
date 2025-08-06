package org.refit.spring.receipt.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.auth.service.UserService;
import org.refit.spring.receipt.dto.*;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.enums.ReceiptFilter;
import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.receipt.enums.ReceiptType;
import org.refit.spring.receipt.service.ReceiptService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@Api(tags = "영수증 API", description = "영수증 등록 및 조회 관련 API입니다.")
@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
public class ReceiptController {
    private final ReceiptService receiptService;


    @ApiOperation(value = "영수증 목록 조회", notes = "전체 영수증을 조회하며, 파라미터로 필터링이 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/list")
    public ResponseEntity<?> getList(
            @ApiIgnore @UserId Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer period,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) ReceiptType type,
            @RequestParam(required = false) ReceiptSort sort,
            @RequestParam(required = false) ReceiptFilter filter) {
        ReceiptListDto dto = receiptService.getFilteredList(userId, cursorId, period, startDate, endDate, type, sort, filter);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "영수증 상세 조회", notes = "영수증 아이디를 활용해 영수증에 기록된 모든 정보를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/detail")
    public ResponseEntity<?> get(
            @ApiIgnore @UserId Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Long receiptId) {
        try {
            Map<String, Object> requiredFields = new HashMap<>();
            requiredFields.put("receiptId", receiptId);
            receiptService.validateRequiredFields(requiredFields);

            ReceiptDetailDto receipt = receiptService.get(userId, cursorId, receiptId);
            return ResponseEntity.ok(receipt);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("해당 영수증이 존재하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 내부 오류가 발생했습니다.");
        }
    }

    @ApiOperation(value = "한 달 비용 조회", notes = "이번 달과 지난 달의 사용 내역 합계를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/monthlyExpense")
    public ResponseEntity<?> getTotal(@ApiIgnore @UserId Long userId) {
        return ResponseEntity.ok(receiptService.getTotal(userId));
    }

    @ApiOperation(value = "법인 처리 불가 항목 조회", notes = "법인 카드를 사용했으나, 처리가 불가한 항목을 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/rejectedList")
    public ResponseEntity<?> getRejected(@ApiIgnore @UserId Long userId) {
        RejectedReceiptListDto dto = receiptService.getRejected(userId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "계좌 이체 후 완료 처리", notes = "법인 처리 불가 항목을 계좌이체 완료 하였을 시 상태를 deposit으로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @PatchMapping("/completeDeposit")
    public ResponseEntity<?> changeState(@ApiIgnore @UserId Long userId,
                                         @RequestBody ChangeStateRequestDto requestDto) {
        try {
            Map<String, Object> requiredFields = new HashMap<>();
            requiredFields.put("receiptId", requestDto.getReceiptId());
            receiptService.validateRequiredFields(requiredFields);

            receiptService.changeState(userId, requestDto.getReceiptId());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 내부 오류가 발생했습니다.");
        }
    }
}
