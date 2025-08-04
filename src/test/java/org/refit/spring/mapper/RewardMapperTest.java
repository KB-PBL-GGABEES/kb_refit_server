//package org.refit.spring.mapper;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.refit.spring.config.RootConfig;
//import org.refit.spring.reward.entity.Reward;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {RootConfig.class})
//@Transactional
//class RewardMapperTest {
//
//    @Autowired
//    private RewardMapper rewardMapper;
//
//    @Test
//    @DisplayName("리워드 생성 테스트")
//    void create() {
//        Reward reward = new Reward();
//        reward.setUserId(1L);
//        reward.setCarbonPoint(10L);
//        reward.setReward(500L);
//        reward.setCreatedAt(new Date());
//        rewardMapper.create(reward);
//        assertNotNull(reward.getRewardId());
//    }
//
//    @Test
//    void getList() {
//    }
//
//    @Test
//    @DisplayName("총 캐시백 조회")
//    void getTotalCashback() {
//        Long total = rewardMapper.getTotalCashback(1L);
//        assertNotNull(total);
//        assertTrue(total >= 0);
//    }
//
//    @Test
//    @DisplayName("총 탄소포인트 조회")
//    void getTotalCarbon() {
//        Long total = rewardMapper.getTotalCarbon(1L);
//        assertNotNull(total);
//        assertTrue(total >= 0);
//    }
//
//    @Test
//    @DisplayName("가장 많이 이용한 카테고리 조회")
//    void getCategory() {
//        String category = rewardMapper.getCategory(1L);
//        assertNotNull(category);
//    }
//
//    @Test
//    @DisplayName("지갑 필요 포인트 조회")
//    void getCost() {
//        Long cost = rewardMapper.getCost(1L);
//        assertNotNull(cost);
//        assertTrue(cost > 0);
//    }
//
//    @Test
//    @DisplayName("지갑 등록")
//    void createPersonal() {
//        rewardMapper.createPersonal(1L, 1L);
//    }
//
//    @Test
//    @DisplayName("총 스타포인트 업데이트")
//    void updateStarPoint() {
//        rewardMapper.updateStarPoint(1L, 9999L);
//    }
//
//    @Test
//    @DisplayName("지갑 보유 여부 확인")
//    void checkPossess() {
//        boolean result = rewardMapper.checkPossess(1L, 1L);
//        assertTrue(result);
//    }
//}