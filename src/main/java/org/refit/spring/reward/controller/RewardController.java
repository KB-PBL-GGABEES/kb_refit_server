package org.refit.spring.reward.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.reward.dto.RewardListDto;
import org.refit.spring.reward.dto.RewardResponseDto;
import org.refit.spring.reward.dto.RewardWalletRequestDto;
import org.refit.spring.reward.dto.RewardWalletResponseDto;
import org.refit.spring.reward.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/reward")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @ApiOperation(value = "리워드 내역 조회", notes = "탄소중립포인트와 리워드 내역을 목록으로 조회합니다.")
    @GetMapping("")
    public ResponseEntity<?> getList(
            @ApiIgnore @UserId Long userId,
            @RequestParam(required = false) Long cursorId) {
        RewardListDto dto = rewardService.getList(userId, cursorId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "포인트 리스트 조회", notes = "메인 페이지 포인트 리스트에서 보여지는 리워드들을 제공합니다.")
    @GetMapping("/getAllPoints")
    public ResponseEntity<?> getTotalPoint(
            @ApiIgnore @UserId Long userId) {
        RewardResponseDto dto = rewardService.getTotal(userId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "지갑 상점 구매", notes = "상점에서 포인트로 지갑을 구매하면 포인트가 차감되고 보유 지갑 브랜드 테이블에 추가됩니다.")
    @PostMapping("/purchase")
    public ResponseEntity<?> getWallet(
            @ApiIgnore @UserId Long userId,
            @RequestBody RewardWalletRequestDto rewardWalletRequestDto) {
        RewardWalletResponseDto dto = rewardService.purchaseWallet(userId, rewardWalletRequestDto);
        return ResponseEntity.ok(dto);
    }
}
