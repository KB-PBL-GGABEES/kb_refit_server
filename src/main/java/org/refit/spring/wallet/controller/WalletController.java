package org.refit.spring.wallet.controller;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.wallet.dto.BadgeResponseDto;
import org.refit.spring.wallet.entity.Badge;
import org.refit.spring.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/badge")
    public ResponseEntity<?> getBadgeList(@UserId Long userId) {
        BadgeResponseDto.BadgeListDto list = walletService.getBadgeList(userId);
        return ResponseEntity.ok(list);
    }

}
