package org.refit.spring.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.refit.spring.config.RootConfig;
import org.refit.spring.mapper.PersonalBadgeMapper;
import org.refit.spring.wallet.dto.BadgeResponseDto;
import org.refit.spring.wallet.entity.PersonalBadge;
import org.refit.spring.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig(classes = RootConfig.class)
public class PresetServiceTest {
    @Autowired
    private WalletService walletService;
    @Autowired
    private PersonalBadgeMapper personalBadgeMapper;

    @Test
    @DisplayName("✅ 프리셋 저장 성공 테스트")
    void savePreset_success() {
        // given
        Long userId = 5L; // 뱃지를 착용하고 있는 사용자 ID
        String presetName = "프리셋착용검증";

        // when
        walletService.saveCurrentWornBadgesAsPreset(userId, presetName);

        // then
        List<BadgeResponseDto.BadgePresetListDto> result = walletService.getMyBadgePresets(userId);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(p -> p.getPresetName().equals(presetName)));

        System.out.println("✅ 프리셋 저장 및 조회 성공: " + presetName);
    }

    @Test
    @DisplayName("✅ 프리셋 목록 조회 테스트")
    void getPresetList_success() {
        // given
        Long userId = 5L;

        // when
        List<BadgeResponseDto.BadgePresetListDto> result = walletService.getMyBadgePresets(userId);

        // then
        assertNotNull(result);
        result.forEach(preset -> {
            System.out.println("📦 프리셋 이름: " + preset.getPresetName());
            System.out.println("📌 포함된 뱃지 수: " + preset.getBadgePresetList().size());
        });
    }

    @Test
    @DisplayName("🗑 프리셋 삭제 테스트")
    void deletePreset_success() {
        // given
        Long userId = 5L;
        List<BadgeResponseDto.BadgePresetListDto> presets = walletService.getMyBadgePresets(userId);
        assertFalse(presets.isEmpty());

        Long targetPresetId = presets.get(0).getPresetId();

        // when
        String result = walletService.deletePreset(userId, targetPresetId);

        // then
        assertEquals("ok", result);
        System.out.println("🗑 삭제된 프리셋 ID: " + targetPresetId);
    }

    @Test
    @DisplayName("🎯 프리셋 착용 성공 테스트")
    void applyPreset_success() {
        // given
        Long userId = 5L;

        // 프리셋이 하나 이상 있어야 하므로 없으면 생성
        List<BadgeResponseDto.BadgePresetListDto> presets = walletService.getMyBadgePresets(userId);
        if (presets.isEmpty()) {
            walletService.saveCurrentWornBadgesAsPreset(userId, "기본프리셋");
            presets = walletService.getMyBadgePresets(userId);
        }

        Long presetId = presets.get(0).getPresetId();

        // when
        BadgeResponseDto.ApplyBadgePresetResultDto result = walletService.applyPreset(userId, presetId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertFalse(result.getWornBadgeIdList().isEmpty());

        System.out.println("🎯 프리셋 착용 성공 - 착용된 뱃지 ID 목록: " + result.getWornBadgeIdList());
    }

    @Test
    @DisplayName("✅ 프리셋 적용 후 실제 착용 뱃지 반영 여부 확인")
    void applyPreset_then_checkDatabaseReflectsWornBadges() {
        // given
        Long userId = 5L;
        String presetName = "프리셋적용검증";

        try {
            // 1. 프리셋 없으면 생성
            List<BadgeResponseDto.BadgePresetListDto> presets = walletService.getMyBadgePresets(userId);
            if (presets.isEmpty()) {
                walletService.saveCurrentWornBadgesAsPreset(userId, presetName);
                presets = walletService.getMyBadgePresets(userId);
            }

            // 2. 사용할 프리셋 ID 가져오기
            Long presetId = presets.get(0).getPresetId();

            List<Long> expectedBadgeIds = presets.stream()
                    .filter(p -> p.getPresetId().equals(presetId))
                    .flatMap(p -> p.getBadgePresetList().stream())
                    .map(BadgeResponseDto.BadgePresetDetailDto::getBadgeId)
                    .collect(Collectors.toList());

            // when
            BadgeResponseDto.ApplyBadgePresetResultDto result = walletService.applyPreset(userId, presetId);

            // then
            assertNotNull(result);
            assertEquals(userId, result.getUserId());
            assertFalse(result.getWornBadgeIdList().isEmpty());
            assertEquals(expectedBadgeIds.size(), result.getWornBadgeIdList().size());
            assertTrue(result.getWornBadgeIdList().containsAll(expectedBadgeIds));

            // 실제 DB에서 isWorn=true인 personalBadge의 badgeId 확인
            List<PersonalBadge> wornBadges = personalBadgeMapper.findWornBadgesByUserId(userId);
            List<Long> wornBadgeIdsFromDb = wornBadges.stream()
                    .map(PersonalBadge::getBadgeId)
                    .collect(Collectors.toList());

            assertEquals(expectedBadgeIds.size(), wornBadgeIdsFromDb.size());
            assertTrue(wornBadgeIdsFromDb.containsAll(expectedBadgeIds));

            System.out.println("✅ 프리셋 적용 DB 반영 테스트 성공!");
            System.out.println("🧷 적용된 프리셋 ID: " + presetId);
            System.out.println("🟡 적용된 뱃지 ID 목록: " + result.getWornBadgeIdList());

        } catch (Exception e) {
            System.out.println("❌ 테스트 중 예외 발생: " + e.getMessage());
            fail("테스트 실패: 예외 발생");
        }
    }

    @Test
    @DisplayName("❌ 프리셋 미보유 상태에서 삭제 요청 시 실패")
    void deletePreset_notOwned_fail() {
        // given
        Long userId = 5L;
        Long fakePresetId = 9999L; // 존재하지 않거나 내가 소유하지 않은 프리셋 ID

        // when
        String result = walletService.deletePreset(userId, fakePresetId);

        // then
        assertNull(result);
        System.out.println("❌ 프리셋 미보유 상태에서 삭제 실패 확인 - presetId: " + fakePresetId);
    }

    @Test
    @DisplayName("✅ 프리셋 착용 후 적용 결과 검증")
    void applyPreset_resultCheck() {
        // given
        Long userId = 5L;

        // 프리셋이 없으면 하나 생성
        List<BadgeResponseDto.BadgePresetListDto> presets = walletService.getMyBadgePresets(userId);
        if (presets.isEmpty()) {
            walletService.saveCurrentWornBadgesAsPreset(userId, "적용검증용 프리셋");
            presets = walletService.getMyBadgePresets(userId);
        }

        Long presetId = presets.get(0).getPresetId();

        // when
        BadgeResponseDto.ApplyBadgePresetResultDto result = null;
        try {
            result = walletService.applyPreset(userId, presetId);
        } catch (Exception e) {
            fail("❌ 프리셋 적용 중 예외 발생: " + e.getMessage());
        }

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertFalse(result.getWornBadgeIdList().isEmpty());

        // 실제 착용된 뱃지들과 일치하는지 확인
        List<PersonalBadge> wornBadges = personalBadgeMapper.findWornBadgesByUserId(userId);
        List<Long> wornBadgeIds = wornBadges.stream()
                .map(PersonalBadge::getBadgeId)
                .toList();

        assertTrue(wornBadgeIds.containsAll(result.getWornBadgeIdList()));

        System.out.println("✅ 프리셋 착용 후 실제 적용 결과 검증 성공!");
        System.out.println("👉 적용된 뱃지 ID 목록: " + result.getWornBadgeIdList());
    }
}
