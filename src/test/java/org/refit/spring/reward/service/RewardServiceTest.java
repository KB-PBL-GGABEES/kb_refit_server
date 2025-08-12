package org.refit.spring.reward.service;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.refit.spring.config.RootConfig;
import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.reward.dto.*;
import org.refit.spring.reward.entity.Reward;
import org.refit.spring.reward.enums.RewardType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig(classes = {RootConfig.class})
@Log4j
class RewardServiceTest {

    @Autowired
    private RewardService service;

    @DisplayName("리워드 생성 테스트")
    @Test
    void create() {
        Reward result = service.create(100L, 10000L, 5L, 496L);

        assertEquals(100L, (long) result.getCarbonPoint());
        assertEquals( 10000L * 0.05, (long) result.getReward());
        assertEquals(5L, result.getUserId());
        assertNotNull(result.getCreatedAt());
    }

    @DisplayName("리워드 목록 조회 테스트")
    @Test
    void getList() {
        Long userId = 5L;
        RewardListRequestDto requestDto = new RewardListRequestDto();
        requestDto.setSize(10L);
        requestDto.setCursorId(null);
        requestDto.setSort(ReceiptSort.LATEST);
        requestDto.setPeriod(1);
        requestDto.setStartDate(null);
        requestDto.setEndDate(null);
        requestDto.setType(RewardType.ALL);

        RewardListCursorDto dto = service.getList(userId, requestDto);

        assertNotNull(dto);
        assertEquals(5L, userId);
        List<Reward> list = dto.getRewardList();
        assertNotNull(list);
        if (!list.isEmpty()) {
            assertEquals(5L, (long) list.get(0).getUserId());
        }
    }


    @DisplayName("리워드 총합 테스트")
    @Test
    void getTotal() {
        RewardSummaryDto dto = service.getTotal(1L);
        assertNotNull(dto);
        assertTrue(dto.getTotalCarbonPoint() >= 0);
        assertTrue(dto.getTotalCashback() >= 0);
        assertNotNull(dto.getCategory());
    }


    @DisplayName("지갑 구매 테스트")
    @Test
    void purchaseWallet() {
        RewardWalletRequestDto requestDto = new RewardWalletRequestDto();
        requestDto.setWalletId(1L);
        RewardWalletResponseDto responseDto = service.purchaseWallet(1L, requestDto);
        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getUserId());
        assertEquals(1L, responseDto.getWalletId());
        assertTrue(responseDto.getWalletCost() > 0);
        assertTrue(responseDto.getTotalStarPoint() >= 0);
    }
}