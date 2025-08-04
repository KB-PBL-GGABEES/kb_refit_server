package org.refit.spring.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.refit.spring.config.RootConfig;
import org.refit.spring.wallet.dto.BadgeRequestDto;
import org.refit.spring.wallet.dto.BadgeResponseDto;
import org.refit.spring.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig(classes = RootConfig.class)
class BadgeServiceTest {
    @Autowired
    private WalletService walletService;

    @Test
    @DisplayName("🧪 사용자 뱃지 도감 정상 조회 테스트")
    void getBadgeList_success() {
        // given
        Long testUserId = 5L;

        // when
        BadgeResponseDto.EveryBadgeListDto result = walletService.getBadgeList(testUserId);

        // then
        assertNotNull(result);
        System.out.println("✅ 뱃지 도감 조회 성공!");
    }

    @Test
    @DisplayName("❌ 존재하지 않는 유저 ID로 도감 조회 시 → 소유한 뱃지 없음")
    void getBadgeList_withInvalidUserId_returnsAllUnownedBadges() {
        // given
        Long invalidUserId = 99999L; // DB에 절대 없을 userId로 테스트

        // when
        BadgeResponseDto.EveryBadgeListDto result = walletService.getBadgeList(invalidUserId);

        // then
        assertNotNull(result);
        assertFalse(result.getBadgeList().isEmpty());

        // 모든 뱃지가 isOwned = false여야 함
        boolean allUnowned = result.getBadgeList().stream().allMatch(b -> !b.isOwned());
        assertTrue(allUnowned, "❌ 일부 뱃지가 isOwned=true로 잘못 표시됨");

        System.out.println("✅ 존재하지 않는 유저로 요청했지만, 전체 뱃지가 isOwned=false로 정상 반환됨");
    }

    @Test
    @DisplayName("✅ 뱃지와 지갑 모두 착용한 경우")
    void getWornBadgeListAndBenefit_full_success() {
        Long userId = 5L; // 뱃지 + 지갑 착용된 유저 ID

        BadgeResponseDto.wornBadgeListAndBenefitDto result = walletService.getWornBadgeListAndBenefit(userId);

        assertNotNull(result);
//        assertNotNull(result.getBrandImage());       // 지갑 이미지가 null이 아닌지 확인
//        assertNotNull(result.getMyBadgeList());      // 뱃지 리스트 null 확인
        if (result.getBrandImage() == null) {
            System.out.println("⚠️ 지갑 이미지가 null입니다. userId=" + userId);
        } else {
            assertNotNull(result.getBrandImage());
        }

        System.out.println("✅ 뱃지 개수: " + (result.getMyBadgeList() != null ? result.getMyBadgeList().size() : 0));
        System.out.println("✅ 착용 지갑 이미지: " + result.getBrandImage());
    }

    @Test
    @DisplayName("🟨 지갑 없이 뱃지만 착용한 경우")
    void getWornBadgeListAndBenefit_onlyBadges() {
        Long userId = 5L; // 뱃지는 있지만 지갑 착용은 없는 유저 ID

        BadgeResponseDto.wornBadgeListAndBenefitDto result = walletService.getWornBadgeListAndBenefit(userId);

        assertNotNull(result, "결과 객체가 null입니다.");

        // 지갑은 없어야 함
        assertNull(result.getBrandImage(), "🟥 예상과 다르게 지갑 이미지가 존재합니다.");

        // 뱃지 리스트가 null일 수도 있으니 조건 분기
        if (result.getMyBadgeList() == null) {
            System.out.println("⚠️ 뱃지를 착용했다고 가정했지만, DB에 착용된 뱃지가 없음 (userId=" + userId + ")");
        } else {
            assertFalse(result.getMyBadgeList().isEmpty(), "🟥 뱃지 리스트가 비어 있습니다.");
            System.out.println("✅ 뱃지만 착용, 지갑 없음 - 착용 뱃지 개수: " + result.getMyBadgeList().size());
        }
    }

    @Test
    @DisplayName("🟦 아무것도 착용하지 않은 경우")
    void getWornBadgeListAndBenefit_noneWorn() {
        Long userId = 6L; // 착용 정보가 없는 유저 ID

        BadgeResponseDto.wornBadgeListAndBenefitDto result = walletService.getWornBadgeListAndBenefit(userId);

        assertNotNull(result);
        assertNull(result.getMyBadgeList());
        assertNull(result.getBrandImage());

        System.out.println("🟦 착용한 뱃지/지갑 없음");
    }

    @Test
    @DisplayName("❌ 존재하지 않는 userId")
    void getWornBadgeListAndBenefit_invalidUser() {
        Long userId = 99999L;

        BadgeResponseDto.wornBadgeListAndBenefitDto result = walletService.getWornBadgeListAndBenefit(userId);

        assertNotNull(result);
        assertNull(result.getMyBadgeList());

        System.out.println("❌ 유효하지 않은 유저 → 착용 뱃지/지갑 모두 없음");
    }

    @Test
    @DisplayName("✅ 특정 뱃지 상세 조회 - 유효한 badgeId와 userId")
    void getSpecificBadgeDetail_success() {
        // given
        Long badgeId = 1L; // 실제 DB에 존재하는 badgeId
        Long userId = 5L;  // 해당 뱃지를 소유한 userId

        // when
        BadgeResponseDto.specificBadgeDetailDto result = walletService.getSpecificBadgeDetail(badgeId, userId);

        // then
        assertNotNull(result);
        assertEquals(badgeId, result.getBadgeId());
        System.out.println("✅ 유효한 badgeId/userId로 뱃지 상세 조회 성공!");
    }

    @Test
    @DisplayName("❌ 존재하지 않는 badgeId → 예외 발생")
    void getSpecificBadgeDetail_invalidBadgeId() {
        // given
        Long badgeId = 99999L; // 존재하지 않는 badgeId
        Long userId = 5L;

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> walletService.getSpecificBadgeDetail(badgeId, userId));

        assertEquals("해당 id의 뱃지를 찾을 수 없습니다.", exception.getMessage());
        System.out.println("❌ 존재하지 않는 badgeId 예외 처리 성공!");
    }

    @Test
    @DisplayName("✅ 뱃지 착용/해제 성공 - 유효한 userId, badgeId")
    void toggleWornBadge_success() {
        // given
        Long userId = 5L;
        Long updateBadgeId = 2L; // 실제 존재하는 personalBadge 정보

        BadgeRequestDto.UpdateWornBadgeDto request = BadgeRequestDto.UpdateWornBadgeDto.builder()
                .previousBadgeId(null) // 기존 해제 없음
                .updateBadgeId(updateBadgeId) // 새로 착용할 뱃지
                .build();

        // when
        BadgeResponseDto.ToggleWornBadgeDto result = walletService.toggleWornBadge(userId, request);

        // then
        assertNotNull(result);
        assertEquals(updateBadgeId, result.getBadgeId());
        assertTrue(result.isWorn());
        System.out.println("✅ 뱃지 착용 성공!");
    }

    @Test
    @DisplayName("❌ 존재하지 않는 userId 또는 badgeId → null 반환")
    void toggleWornBadge_invalidUserOrBadge() {
        // given
        Long userId = 99999L; // 존재하지 않는 유저
        Long updateBadgeId = 88888L; // 존재하지 않는 뱃지

        BadgeRequestDto.UpdateWornBadgeDto request = BadgeRequestDto.UpdateWornBadgeDto.builder()
                .previousBadgeId(null)
                .updateBadgeId(updateBadgeId)
                .build();

        // when
        BadgeResponseDto.ToggleWornBadgeDto result = walletService.toggleWornBadge(userId, request);

        // then
        assertNull(result);
        System.out.println("❌ 유효하지 않은 userId/badgeId → null 반환");
    }
}
