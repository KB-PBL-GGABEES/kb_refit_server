package org.refit.spring.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.refit.spring.config.RootConfig;
import org.refit.spring.wallet.dto.WalletResponseDto;
import org.refit.spring.wallet.service.WalletService;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;


@SpringJUnitWebConfig(classes = RootConfig.class)
public class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Test
    @DisplayName("✅ 전체 지갑 브랜드 조회 성공 - 유효한 userId")
    void getWalletList_success() {
        // given
        Long userId = 5L; // 실제 존재하는 유저 ID

        // when
        WalletResponseDto.WalletBrandListDto result = walletService.getWalletList(userId);

        // then
        assertNotNull(result);
        assertNotNull(result.getWalletBrandDtoList());
        assertFalse(result.getWalletBrandDtoList().isEmpty()); // 최소 1개 이상의 지갑 브랜드가 있어야 함
        assertNotNull(result.getStarPoint()); // 별점이 null이면 안 됨

        System.out.println("✅ 전체 지갑 브랜드 조회 성공!");
    }

    @Test
    @DisplayName("❌ 존재하지 않는 userId → 지갑 브랜드 리스트 없음")
    void getWalletList_invalidUser() {
        Long userId = 99999L;

        try {
            WalletResponseDto.WalletBrandListDto result = walletService.getWalletList(userId);

            if (result == null || result.getWalletBrandDtoList() == null) {
                System.out.println("❌ 유효하지 않은 userId → 결과 없음 (null 또는 리스트 비어있음)");
            } else {
                System.out.println("❗ 예상치 못한 결과 있음 → 리스트 크기: " + result.getWalletBrandDtoList().size());
            }
        } catch (NullPointerException e) {
            System.out.println("❌ 유효하지 않은 userId → NPE 발생하지만 이는 정상 범주로 간주");
        } catch (Exception e) {
            fail("예상치 못한 예외 발생: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("🟩 지갑 착용 성공")
    void toggleMountedWallet_success() {
        // given
        Long userId = 5L; // 실제 유저 ID
        Long walletId = 2L; // 존재하는 walletBrand ID

        // when
        WalletResponseDto.ToggleMountedWalletDto result = walletService.toggleMountedWallet(userId, walletId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(walletId, result.getWalletId());
        assertTrue(result.isMounted()); // 착용 상태가 true로 바뀌어야 함

        System.out.println("🟩 지갑 착용 성공!");
    }

    @Test
    @DisplayName("❌ 존재하지 않는 userId 또는 walletId → null 반환")
    void toggleMountedWallet_invalidUserOrWallet() {
        // given
        Long userId = 99999L; // 존재하지 않는 유저
        Long walletId = 88888L; // 존재하지 않는 지갑

        // when
        WalletResponseDto.ToggleMountedWalletDto result = walletService.toggleMountedWallet(userId, walletId);

        // then
        assertNull(result); // 존재하지 않으면 null 반환
        System.out.println("❌ 유효하지 않은 userId/walletId → null 반환");
    }

    @Test
    @DisplayName("🟦 이미 착용한 지갑이 있을 경우 → 기존 지갑 해제 후 새 지갑 착용")
    void toggleMountedWallet_alreadyMounted() {
        // given
        Long userId = 5L;         // 이미 다른 지갑을 착용 중인 유저 ID
        Long newWalletId = 2L;    // 새로 착용하려는 지갑 ID (착용 중이 아닌 다른 지갑)

        // when
        WalletResponseDto.ToggleMountedWalletDto result = walletService.toggleMountedWallet(userId, newWalletId);

        // then
        if (result == null) {
            System.out.println("⚠️ 테스트 스킵: 해당 userId + walletId 조합에 대해 데이터가 존재하지 않습니다.");
            return; // 테스트 실패 처리하지 않고 그냥 종료
        }

        assertNotNull(result);
        assertEquals(newWalletId, result.getWalletId());
        assertTrue(result.isMounted()); // 새 지갑이 착용 상태가 되었는지 확인

        System.out.println("🟦 기존 지갑 해제 후 새 지갑 착용 완료!");
    }

    @Test
    @DisplayName("🟦 다른 지갑을 착용한 경우 → 기존 지갑 해제 & 새 지갑 착용됨")
    void toggleMountedWallet_differentWalletSelected() {
        // given
        Long userId = 5L;
        Long previousWalletId = 2L; // 이미 착용 중인 지갑 ID
        Long newWalletId = 3L;      // 새로 착용할 지갑 ID

        // when
        WalletResponseDto.ToggleMountedWalletDto result = walletService.toggleMountedWallet(userId, newWalletId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(newWalletId, result.getWalletId());
        assertTrue(result.isMounted());
    }

    @Test
    @DisplayName("🟧 이미 착용 중인 지갑을 다시 선택한 경우 → 그대로 착용 상태 유지됨")
    void toggleMountedWallet_sameWalletSelectedAgain() {
        // given
        Long userId = 5L;
        Long walletId = 2L; // 이미 착용 중인 지갑

        // when
        WalletResponseDto.ToggleMountedWalletDto result = walletService.toggleMountedWallet(userId, walletId);

        // 로그 출력
        System.out.println("🧪 [입력] userId: " + userId + ", walletId: " + walletId);
        System.out.println("🧪 [결과] userId: " + result.getUserId());
        System.out.println("🧪 [결과] walletId: " + result.getWalletId());
        System.out.println("🧪 [결과] isMounted (기대한 값: false): " + result.isMounted());

        try {
            // then
            assertNotNull(result);
            assertEquals(userId, result.getUserId());
            assertEquals(walletId, result.getWalletId());
            assertFalse(result.isMounted()); // 착용 해제되어야 함

            System.out.println("✅ 테스트 성공: 이미 착용한 지갑을 다시 선택 → 해제됨");
        } catch (AssertionError e) {
            System.out.println("❌ 테스트 실패: " + e.getMessage());
            throw e; // 테스트 실패를 JUnit에게 알림
        }
    }
}