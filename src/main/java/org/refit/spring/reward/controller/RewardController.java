package org.refit.spring.reward.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.reward.dto.RewardListDto;
import org.refit.spring.reward.dto.RewardResponseDto;
import org.refit.spring.reward.dto.RewardWalletRequestDto;
import org.refit.spring.reward.dto.RewardWalletResponseDto;
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

    @ApiOperation(value = "리워드 내역 조회", notes = "탄소중립포인트와 리워드 내역을 목록으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/list")
    public ResponseEntity<?> getList(
            @ApiIgnore @UserId Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer period) {
        RewardListDto dto;
        if (period != null && period > 0) dto = rewardService.getListMonths(userId, cursorId, period);
        else dto = rewardService.getList(userId, cursorId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "설정한 기간 만큼의 리워드 내역 조회", notes = "시작 날짜와 종료 날짜를 선택해 기간별 조회가 가능합니다.")
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
        RewardListDto dto = rewardService.getListPeriod(userId, cursorId, startDate, endDate);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "포인트 리스트 조회", notes = "메인 페이지 포인트 리스트에서 보여지는 리워드들을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/getAllPoints")
    public ResponseEntity<?> getTotalPoint(
            @ApiIgnore @UserId Long userId) {
        RewardResponseDto dto = rewardService.getTotal(userId);
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
