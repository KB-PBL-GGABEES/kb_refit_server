package org.refit.spring.receipt.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.auth.service.UserService;
import org.refit.spring.receipt.dto.ReceiptDetailDto;
import org.refit.spring.receipt.dto.ReceiptListDto;
import org.refit.spring.receipt.dto.ReceiptRequestDto;
import org.refit.spring.receipt.dto.ReceiptResponseDto;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.service.ReceiptService;
import org.refit.spring.reward.entity.Reward;
import org.refit.spring.reward.service.RewardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;
import java.sql.SQLException;
import java.util.Date;


@Api(tags = "영수증 API", description = "영수증 등록 및 조회 관련 API입니다.")
@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
public class ReceiptController {
    private final ReceiptService receiptService;
    private final RewardService rewardService;

    private final UserService userService;

    private static final Long CARBON_POINT = 100L;

    @ApiOperation(value = "영수증 등록", notes = "결제 시 영수증이 생성됩니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @PostMapping("/create")
    public ResponseEntity<?> create(@ApiIgnore @UserId Long userId, @RequestBody ReceiptRequestDto receiptRequestDto) throws SQLException {
        Receipt receipt = receiptService.create(receiptRequestDto, userId);
        Reward reward = rewardService.create(CARBON_POINT, receipt.getTotalPrice(), userId);
        userService.updatePoint(userId, reward.getCarbonPoint(), reward.getReward());
        ReceiptResponseDto dto = ReceiptResponseDto.from(receipt, userId, reward.getCarbonPoint(), reward.getReward());
        URI location = URI.create("/receipt/" + receipt.getReceiptId());

        return ResponseEntity.created(location).body(dto);
    }

    @ApiOperation(value = "환불 영수증 등록", notes = "결제 시 생성된 영수증 아이디를 이용해 환불 영수증을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @PostMapping("/refund")
    public ResponseEntity<?> refund(@ApiIgnore @UserId Long userId, @RequestParam("receiptId") Long receiptId) {
        Receipt receipt = receiptService.refund(userId, receiptId);
        Reward reward = rewardService.create(-CARBON_POINT, receipt.getTotalPrice(), userId);
        userService.updatePoint(userId, reward.getCarbonPoint(), reward.getReward());
        ReceiptResponseDto dto = ReceiptResponseDto.from(receipt, userId, reward.getCarbonPoint(), reward.getReward());
        URI location = URI.create("/receipt/" + receipt.getReceiptId());
        return ResponseEntity.created(location).body(dto);
    }

    @ApiOperation(value = "영수증 목록 조회", notes = "전체 영수증을 조회하며, period 값으로 기간별 조회가 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/list")
    public ResponseEntity<?> getList(
            @ApiIgnore @UserId Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer period) {
        ReceiptListDto dto;
        if (period != null && period > 0) dto = receiptService.getListMonths(userId, cursorId, period);
        else dto = receiptService.getList(userId, cursorId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "설정된 기간 만큼의 영수증 목록 조회", notes = "시작 날짜와 종료 날짜를 선택해 기간별 조회가 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/list/period")
    public ResponseEntity<?> getListPeriod(
            @ApiIgnore @UserId Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        ReceiptListDto dto = receiptService.getListPeriod(userId, cursorId, startDate, endDate);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "영수증 상세 조회", notes = "영수증 아이디를 활용해 영수증에 기록된 모든 정보를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/get")
    public ResponseEntity<?> get(
            @ApiIgnore @UserId Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam("receiptId") Long receiptId) {
        ReceiptDetailDto receipt = receiptService.get(userId, cursorId, receiptId);
        return ResponseEntity.ok(receipt);
    }

    @ApiOperation(value = "한 달 비용 조회", notes = "이번 달과 지난 달의 사용 내역 합계를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/monthlyReport")
    public ResponseEntity<?> getTotal(@ApiIgnore @UserId Long userId) {
        return ResponseEntity.ok(receiptService.getTotal(userId));
    }

    @ApiOperation(value = "법인 처리 불가 항목 조회", notes = "법인 카드를 사용했으나, 처리가 불가한 항목을 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/getRejected")
    public ResponseEntity<?> getRejected(@ApiIgnore @UserId Long userId) {
        return ResponseEntity.ok(receiptService.getRejected(userId));
    }

    @ApiOperation(value = "계좌 이체 후 완료 처리", notes = "법인 처리 불가 항목을 계좌이체 완료 하였을 시 상태를 deposit으로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @PatchMapping("/completeDeposit")
    public ResponseEntity<?> changeState(@ApiIgnore @UserId Long userId,
                                         @RequestBody Long receiptProcessId) {
        receiptService.changeState(userId, receiptProcessId);
        return ResponseEntity.ok().build();
    }
}
