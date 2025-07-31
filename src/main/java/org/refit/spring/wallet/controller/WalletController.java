package org.refit.spring.wallet.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.wallet.dto.BadgeRequestDto;
import org.refit.spring.wallet.dto.BadgeResponseDto;
import org.refit.spring.wallet.dto.WalletResponseDto;
import org.refit.spring.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "전자지갑 API", description = "뱃지 및 지갑 디자인, 프리셋 관련 API입니다.")
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @ApiOperation(value = "뱃지 도감 조회", notes = "전체 뱃지 도감 리스트와 현재 보유 여부를 확인할 수 있습니다.")
    @GetMapping("/badge")
    public ResponseEntity<BadgeResponseDto.BadgeListDto> getBadgeList(@ApiIgnore @UserId Long userId) {
        BadgeResponseDto.BadgeListDto list = walletService.getBadgeList(userId);
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "내가 착용한 뱃지 및 혜택 조회", notes = "현재 사용자가 착용한 뱃지의 리스트와 그에 해당하는 혜택을 조회할 수 있습니다.")
    @GetMapping("/badge/home")
    public ResponseEntity<?> myWornBadgeList(@ApiIgnore @UserId Long userId) {
        BadgeResponseDto.wornBadgeListAndBenefitDto result = walletService.getWornBadgeListAndBenefit(userId);
        if (result.getMyBadgeList() == null || result.getMyBadgeList().isEmpty()) {
            return ResponseEntity.ok("현재 착용 중인 뱃지가 없습니다.");
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "특정 뱃지 정보 조회", notes = "특정한 뱃지의 상세 정보를 조회할 수 있습니다.")
    @GetMapping("/badge/detail/{badgeId}")
    public ResponseEntity<BadgeResponseDto.specificBadgeDetailDto> getBadgeDetail(@ApiIgnore @UserId Long userId, @PathVariable("badgeId") Long badgeId) {
        BadgeResponseDto.specificBadgeDetailDto result = walletService.getSpecificBadgeDetail(badgeId, userId);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "뱃지 장착/해제", notes = "최대 4개까지 뱃지를 장착할 수 있으며, 초과 시 기존 뱃지를 교체합니다.")
    @PatchMapping("/badge/equip")
    public ResponseEntity<?> updateMyWornBadge(@ApiIgnore @UserId Long userId, @RequestBody BadgeRequestDto.UpdateWornBadgeDto requestDto) {
        BadgeResponseDto.ToggleWornBadgeDto result = walletService.toggleWornBadge(userId, requestDto);

        if (result == null) {
            Map<String, String> error = new HashMap<>();

            if (requestDto.getUpdateBadgeId() == null && requestDto.hasBadgeToUnwear()) {
                error.put("message", "보유하고 있지 않은 뱃지입니다.");
            } else if (!requestDto.hasBadgeToUnwear() && requestDto.getUpdateBadgeId() != null) {
                error.put("message", "착용하려는 뱃지가 존재하지 않거나 보유하고 있지 않습니다.");
            } else if (requestDto.hasBadgeToUnwear() && requestDto.getUpdateBadgeId() != null) {
                error.put("message", "착용 또는 해제하려는 뱃지가 존재하지 않습니다.");
            } else {
                error.put("message", "요청이 올바르지 않습니다.");
            }

            return ResponseEntity.badRequest().body(error);
        }

        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "지갑 브랜드 상점", notes = "전자지갑의 브랜드 별 지갑 디자인을 구매할 수 있는 상점입니다.")
    @GetMapping("/brand")
    public ResponseEntity<WalletResponseDto.WalletBrandListDto> getWalletStore(@ApiIgnore @UserId Long userId) {
        WalletResponseDto.WalletBrandListDto result = walletService.getWalletList(userId);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "지갑 브랜드 상세 조회", notes = "전자지갑 브랜드 디자인의 상세 정보를 확인할 수 있습니다.")
    @GetMapping("/brand/detail/{walletId}")
    public ResponseEntity<?> getWalletDetail(@ApiIgnore @UserId Long userId, @PathVariable("walletId") Long walletId) {
        WalletResponseDto.WalletBrandDetailDto result = walletService.getWalletDetail(userId, walletId);

        if (result == null) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "지갑 디자인 착용/해제", notes = "전자지갑 브랜드 디자인을 착용하고 해제할 수 있습니다.")
    @PatchMapping("/brand/detail/{walletId}")
    public ResponseEntity<?> updateWalletDesign(@ApiIgnore @UserId Long userId, @PathVariable("walletId") Long walletId) {
        WalletResponseDto.ToggleMountedWalletDto result = walletService.toggleMountedWallet(userId, walletId);
        if (result == null) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "뱃지 프리셋 저장", notes = "현재 착용한 뱃지들을 하나의 프리셋으로 저장합니다.")
    @PostMapping("/badge/preset")
    public ResponseEntity<Void> savePreset(@ApiIgnore @UserId Long userId, @RequestBody BadgeRequestDto.SaveBadgePresetDto request) {
        try {
            walletService.saveCurrentWornBadgesAsPreset(userId, request.getPresetName());
            return ResponseEntity.ok().build(); //200
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiOperation(value = "뱃지 프리셋 조회", notes = "현재 로그인한 유저의 뱃지 프리셋 목록을 조회합니다.")
    @GetMapping("/badge/preset")
    public ResponseEntity<?> savePreset(@ApiIgnore @UserId Long userId) {
        List<BadgeResponseDto.BadgePresetListDto> result = walletService.getMyBadgePresets(userId);

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "뱃지 프리셋 삭제", notes = "현재 로그인 한 유저의 프리셋을 선택하여 삭제합니다.")
    @DeleteMapping("/badge/preset/{presetId}")
    public ResponseEntity<Void> deletePreset(@ApiIgnore @UserId Long userId, @PathVariable("presetId") Long presetId) {
        String result = walletService.deletePreset(userId, presetId);
        if (result == null || result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "뱃지 프리셋 착용", notes = "선택한 프리셋을 한 번에 적용하여 기존 착용 뱃지를 모두 교체합니다.")
    @PatchMapping("/badge/preset/apply/{presetId}")
    public ResponseEntity<?> applyPreset(@ApiIgnore @UserId Long userId, @PathVariable("presetId") Long presetId) {
        BadgeResponseDto.ApplyBadgePresetResultDto result = walletService.applyPreset(userId, presetId);
        if (result == null || result.getWornBadgeIdList().isEmpty()) {
            return ResponseEntity.noContent().build();//204
        }
        return ResponseEntity.ok(result);
    }
}
