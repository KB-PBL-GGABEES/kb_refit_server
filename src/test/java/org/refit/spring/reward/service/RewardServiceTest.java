package org.refit.spring.reward.service;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refit.spring.auth.entity.User;
import org.refit.spring.config.RootConfig;
import org.refit.spring.reward.dto.RewardListDto;
import org.refit.spring.reward.dto.RewardResponseDto;
import org.refit.spring.reward.dto.RewardWalletRequestDto;
import org.refit.spring.reward.dto.RewardWalletResponseDto;
import org.refit.spring.reward.entity.Reward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j
class RewardServiceTest {

    @Autowired
    private RewardService service;

    @DisplayName("리워드 생성 테스트")
    @Test
    void create() {
        Reward result = service.create(100L, 10000L, 1L);

        assertEquals(100L, (long) result.getCarbonPoint());
        assertEquals( 10000L * 0.05, (long) result.getReward());
        assertEquals(1L, result.getUserId());
        assertNotNull(result.getCreatedAt());
    }

    @DisplayName("리워드 목록 조회 테스트")
    @Test
    void getList() {
        RewardListDto dto = service.getList(1L, null);
        assertNotNull(dto);
        assertEquals(1L, dto.getUserId());
        List<Reward> list = dto.getRewardList();
        assertNotNull(list);
        if (!list.isEmpty()) {
            assertTrue(list.get(0).getUserId().equals(1L));
        }
    }


    @DisplayName("리워드 총합 테스트")
    @Test
    void getTotal() {
        RewardResponseDto dto = service.getTotal(1L);
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