package org.refit.spring.wallet.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.wallet.dto.BadgeResponseDto;
import org.refit.spring.wallet.entity.Badge;
import org.refit.spring.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @ApiOperation(value = "뱃지 도감 조회", notes = "전체 뱃지 도감 리스트와 현재 보유 여부를 확인할 수 있습니다.")
    @GetMapping("/badge")
    public ResponseEntity<BadgeResponseDto.BadgeListDto> getBadgeList(@UserId Long userId) {
        BadgeResponseDto.BadgeListDto list = walletService.getBadgeList(userId);
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "내가 착용한 뱃지 및 혜택 조회", notes = "현재 사용자가 착용한 뱃지의 리스트와 그에 해당하는 혜택을 조회할 수 있습니다.")
    @GetMapping("/badge/home")
    public ResponseEntity<?> myWornBadgeList(@UserId Long userId) {
        BadgeResponseDto.wornBadgeListAndBenefitDto result = walletService.getWornBadgeListAndBenefit(userId);
        if (result.getMyBadgeList() == null || result.getMyBadgeList().isEmpty()) {
            return ResponseEntity.ok("현재 착용 중인 뱃지가 없습니다.");
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "특정 뱃지 정보 조회", notes = "특정한 뱃지의 상세 정보를 조회할 수 있습니다.")
    @GetMapping("/badge/detail/{badgeId}")
    public ResponseEntity<BadgeResponseDto.specificBadgeDetailDto> getBadgeDetail(@UserId Long userId, @PathVariable("badgeId") Long badgeId) {
        BadgeResponseDto.specificBadgeDetailDto result = walletService.getSpecificBadgeDetail(badgeId, userId);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "뱃지 장착/해제", notes = "특정 뱃지를 장착하고 해제할 수 있습니다.")
    @PatchMapping("/badge/{badgeId}")
    public ResponseEntity<?> updateMyWornBadge(@UserId Long userId, @PathVariable("badgeId") Long badgeId) {
        BadgeResponseDto.toggleWornBadgeDto result = walletService.toggleWornBadge(userId, badgeId);
        return ResponseEntity.ok(result);
    }
}
