package org.refit.spring.reward.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.reward.dto.RewardListDto;
import org.refit.spring.reward.dto.RewardSummaryDto;
import org.refit.spring.reward.dto.RewardWalletRequestDto;
import org.refit.spring.reward.dto.RewardWalletResponseDto;
import org.refit.spring.reward.enums.RewardType;
import org.refit.spring.reward.service.RewardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;
import java.util.Date;

@Api(tags = "리워드 API", description = "리워드 내역과 메인 화면용 포인트 리스트 관련 API입니다.")
@RestController
@RequestMapping("/api/reward")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @ApiOperation(value = "리워드 내역 조회", notes = "탄소중립포인트와 리워드 내역을 목록으로 조회하며, 파라미터를 이용해 필터링할 수 있습니다.")
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
            @RequestParam(required = false) RewardType type,
            @RequestParam(required = false) ReceiptSort sort) {
        RewardListDto dto = rewardService.getList(userId, cursorId, period, startDate, endDate, type, sort);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "포인트 리스트 조회", notes = "메인 페이지 포인트 리스트에서 보여지는 리워드들을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/rewardSummary")
    public ResponseEntity<?> getTotalPoint(
            @ApiIgnore @UserId Long userId) {
        RewardSummaryDto dto = rewardService.getTotal(userId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "지갑 상점 구매", notes = "상점에서 포인트로 지갑을 구매하면 포인트가 차감되고 보유 지갑 브랜드 테이블에 추가됩니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @PostMapping("/purchase")
    public ResponseEntity<?> getWallet(
            @ApiIgnore @UserId Long userId,
            @RequestBody RewardWalletRequestDto rewardWalletRequestDto) {
        RewardWalletResponseDto dto = rewardService.purchaseWallet(userId, rewardWalletRequestDto);
        URI location = URI.create("/reward/" + dto.getWalletId());
        return ResponseEntity.created(location).body(dto);
    }
}
