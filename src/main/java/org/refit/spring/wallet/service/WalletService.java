package org.refit.spring.wallet.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.entity.User;
import org.refit.spring.mapper.*;
import org.refit.spring.wallet.dto.BadgeRequestDto;
import org.refit.spring.wallet.dto.BadgeResponseDto;
import org.refit.spring.wallet.dto.WalletResponseDto;
import org.refit.spring.wallet.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class WalletService {
    private final BadgeMapper badgeMapper;
    private final PersonalBadgeMapper personalBadgeMapper;
    private final UserMapper userMapper;
    private final WalletBrandMapper walletBrandMapper;
    private final PersonalWalletBrandMapper personalWalletBrandMapper;
    private final BadgePresetMapper badgePresetMapper;

    public BadgeResponseDto.EveryBadgeListDto getBadgeList(Long userId) {
        List<Badge> allBadges = badgeMapper.findAll(); // 전체 뱃지 목록
        List<Long> ownedBadgeIds = personalBadgeMapper.findBadgeIdsByUserId(userId); // 보유한 뱃지 ID

        Set<Long> ownedSet = ownedBadgeIds.stream().collect(Collectors.toSet());

        List<BadgeResponseDto.specificBadgeDetailDto> badgeList = allBadges.stream()
                .map(badge -> BadgeResponseDto.specificBadgeDetailDto.from(badge, ownedSet.contains(badge.getBadgeId())))
                .collect(Collectors.toList());

        return BadgeResponseDto.EveryBadgeListDto.from(badgeList);
    }



    public BadgeResponseDto.wornBadgeListAndBenefitDto getWornBadgeListAndBenefit(Long userId) {
        // 1. 착용한 뱃지 목록 조회
        List<PersonalBadge> wornBadges = personalBadgeMapper.findWornBadgesByUserId(userId);

        // 2. BadgeDetailDto 변환 (없으면 빈 리스트)
        List<BadgeResponseDto.BadgeDetailDto> badgeDetailDtoList = wornBadges.isEmpty()
                ? null // ← 쑤의 요청대로 null로 반환
                : wornBadges.stream()
                .map(pb -> {
                    Badge badge = badgeMapper.findById(pb.getBadgeId());
                    return BadgeResponseDto.BadgeDetailDto.from(badge, pb);
                })
                .collect(Collectors.toList());

        // 3. 착용 중인 지갑 조회
        PersonalWalletBrand mountedWallet = personalWalletBrandMapper.findMountedWalletByUserId(userId);

        WalletBrand walletBrand = null;
        if (mountedWallet != null) {
            walletBrand = walletBrandMapper.findWalletBrandById(mountedWallet.getWalletId());
        }

        // 4. DTO 변환
        return BadgeResponseDto.wornBadgeListAndBenefitDto.from(walletBrand, badgeDetailDtoList);
    }

    public BadgeResponseDto.specificBadgeDetailDto getSpecificBadgeDetail(Long badgeId, Long userId) {
        //1. 뱃지 정보 조회
        Badge badge = badgeMapper.findById(badgeId);
        //2. 유저가 해당 뱃지를 소유 중인지 확인
        if (badge == null) {
            throw new RuntimeException("해당 id의 뱃지를 찾을 수 없습니다.");
        }
        boolean isOwned = personalBadgeMapper.existsByUserIdAndBadgeId(userId, badgeId);

        //3. DTO 반환
        return BadgeResponseDto.specificBadgeDetailDto.from(badge, isOwned);
    }

    public BadgeResponseDto.ToggleWornBadgeDto toggleWornBadge(Long userId, BadgeRequestDto.UpdateWornBadgeDto requestDto) {
        Long previousBadgeId = requestDto.getPreviousBadgeId();
        Long updateBadgeId = requestDto.getUpdateBadgeId();

        // 현재 착용 중인 뱃지 개수
        List<PersonalBadge> wornBadges = personalBadgeMapper.findWornBadgesByUserId(userId);
        int wornCount = wornBadges.size();

        // [1] 해제만 요청한 경우
        if (updateBadgeId == null && previousBadgeId != null) {
            PersonalBadge badgeToUnwear = personalBadgeMapper.findByUserIdAndBadgeId(userId, previousBadgeId);
            if (badgeToUnwear == null) return null;

            personalBadgeMapper.updateBadgeWornStatus(userId, previousBadgeId, false);
            badgeToUnwear.setWorn(false); // 변경 상태 반영
            return BadgeResponseDto.ToggleWornBadgeDto.from(badgeToUnwear);
        }

        // [2] 착용만 요청한 경우
        if (previousBadgeId == null && updateBadgeId != null) {
            if (wornCount >= 4) return null; // 착용 제한 초과

            PersonalBadge badgeToWear = personalBadgeMapper.findByUserIdAndBadgeId(userId, updateBadgeId);
            if (badgeToWear == null) return null;

            personalBadgeMapper.updateBadgeWornStatus(userId, updateBadgeId, true);
            badgeToWear.setWorn(true); // 변경 상태 반영
            return BadgeResponseDto.ToggleWornBadgeDto.from(badgeToWear);
        }

        // [3] 해제 + 착용 둘 다 요청한 경우
        if (previousBadgeId != null && updateBadgeId != null) {
            PersonalBadge badgeToUnwear = personalBadgeMapper.findByUserIdAndBadgeId(userId, previousBadgeId);
            PersonalBadge badgeToWear = personalBadgeMapper.findByUserIdAndBadgeId(userId, updateBadgeId);

            if (badgeToUnwear == null || badgeToWear == null) return null;

            personalBadgeMapper.updateBadgeWornStatus(userId, previousBadgeId, false);
            personalBadgeMapper.updateBadgeWornStatus(userId, updateBadgeId, true);

            badgeToWear.setWorn(true);  // 착용한 뱃지 반환
            return BadgeResponseDto.ToggleWornBadgeDto.from(badgeToWear);
        }

        // [예외] 잘못된 요청
        return null;
    }

    public WalletResponseDto.WalletBrandListDto getWalletList(Long userId) {
        // 1. 유저 정보 가져오기
        User user = userMapper.findByUserId(userId);

        // 2. 전체 지갑 브랜드 리스트
        List<WalletBrand> walletBrandList = walletBrandMapper.findAllWalletBrands();

        // 3. 유저가 보유한 walletId 리스트
        List<Long> ownedWalletIds = personalWalletBrandMapper.findOwnedWalletIdsByUserId(userId);

        // 4. 각 브랜드에 대해 보유 및 착용 여부 계산 후 DTO로 변환
        List<WalletResponseDto.WalletBrandDetailDto> walletBrandDtos = walletBrandList.stream()
                .map(brand -> {
                    boolean isOwned = ownedWalletIds.contains(brand.getWalletId());
                    PersonalWalletBrand personalWalletBrand = isOwned
                            ? personalWalletBrandMapper.findByUserIdAndWalletId(userId, brand.getWalletId())
                            : new PersonalWalletBrand(); // 기본값 객체

                    return WalletResponseDto.WalletBrandDetailDto.from(brand, user, personalWalletBrand, isOwned);
                })
                .collect(Collectors.toList());

        // 5. 최종 응답 DTO 생성
        return WalletResponseDto.WalletBrandListDto.from(walletBrandDtos, user);
    }

    public WalletResponseDto.WalletBrandDetailDto getWalletDetail(Long userId, Long walletId) {
        WalletBrand brand = walletBrandMapper.findWalletBrandById(walletId);
        if (brand == null) {
            return null; //204처리
        }
        User user = userMapper.findByUserId(userId);
        PersonalWalletBrand personal = personalWalletBrandMapper.findByUserIdAndWalletId(userId, walletId);

        boolean isOwned = (personal != null);

        return WalletResponseDto.WalletBrandDetailDto.from(brand, user, personal != null ? personal : new PersonalWalletBrand(), isOwned);
    }

    public WalletResponseDto.ToggleMountedWalletDto toggleMountedWallet(Long userId, Long walletId) {
        PersonalWalletBrand target = personalWalletBrandMapper.findByUserIdAndWalletId(userId, walletId);
        if (target == null) {
            return null;
        }

        // 1. 기존에 착용한 지갑 해제 (is_mounted = true → false)
        personalWalletBrandMapper.unmountCurrentWallet(userId);

        // 2. 새 지갑 착용 (is_mounted = false → true)
        personalWalletBrandMapper.mountNewWallet(userId, walletId);

        // 3. 변경된 결과 조회 후 DTO 변환
        PersonalWalletBrand updated = personalWalletBrandMapper.findByUserIdAndWalletId(userId, walletId);
        return WalletResponseDto.ToggleMountedWalletDto.from(updated);
    }

    //프리셋 저장
    public void saveCurrentWornBadgesAsPreset(Long userId, String presetName) {
        // 1. 현재 착용한 뱃지 조회
        List<PersonalBadge> wornBadges = personalBadgeMapper.findWornBadgesByUserId(userId);

        if (wornBadges.isEmpty()) {
            throw new IllegalStateException("현재 착용 중인 뱃지가 없습니다.");
        }

        // 2. 프리셋 저장
        BadgePreset preset = BadgePreset.builder()
                .presetName(presetName)
                .isApplied(false)
                .userId(userId)
                .build();
        badgePresetMapper.insertBadgePreset(preset);

        // 3. 프리셋 상세 저장 (1~4개 반복)
        for (PersonalBadge badge : wornBadges) {
            BadgePresetDetail detail = BadgePresetDetail.builder()
                    .presetId(preset.getPresetId())
                    .personalBadgeId(badge.getPersonalBadgeId())
                    .build();
            badgePresetMapper.insertPresetDetail(detail);
        }
    }

    //프리셋 조회
    public List<BadgeResponseDto.BadgePresetListDto> getMyBadgePresets(Long userId) {
        // 1. 프리셋 메타 정보 가져오기
        List<BadgePreset> badgePresets = badgePresetMapper.findAllByUserId(userId);

        // 2. 각 프리셋에 대해 디테일 정보 조립
        return badgePresets.stream()
                .map(preset -> {
                    // preset에 포함된 personalBadgeId들 조회
                    List<BadgePresetDetail> details = badgePresetMapper.findAllByPresetId(preset.getPresetId());

                    // personalBadgeId → personalBadge → badgeId → badge
                    List<BadgeResponseDto.BadgePresetDetailDto> badgeDetailDtoList = details.stream()
                            .map(detail -> {
                                PersonalBadge personalBadge = personalBadgeMapper.findById(detail.getPersonalBadgeId());
                                Badge badge = badgeMapper.findById(personalBadge.getBadgeId());
                                return BadgeResponseDto.BadgePresetDetailDto.from(badge, personalBadge);
                            })
                            .collect(Collectors.toList());

                    // 프리셋 DTO로 조립
                    return BadgeResponseDto.BadgePresetListDto.from(preset, badgeDetailDtoList);
                })
                .collect(Collectors.toList());
    }

    public String deletePreset(Long userId, Long presetId) {
        //1. presetId로 해당 프리셋 가져오기
        List<BadgePreset> userPresets = badgePresetMapper.findAllByUserId(userId);

        if (userPresets == null || userPresets.isEmpty()) {
            return null;
        }

        boolean isOwner = userPresets.stream()
                .anyMatch(preset -> preset.getPresetId().equals(presetId));

        if (!isOwner) {
            return null;
        }

        //2. 프리셋 삭제
        badgePresetMapper.deletePresetById(presetId);
        return "ok";
    }

    //프리셋 뱃지 한번에 착용
    public BadgeResponseDto.ApplyBadgePresetResultDto applyPreset(Long userId, Long presetId) {
        //1. 프리셋 보유 검증
        List<BadgePreset> userPresets = badgePresetMapper.findAllByUserId(userId);
        boolean isOwner = userPresets.stream()
                .anyMatch(p -> p.getPresetId().equals(presetId));
        if (!isOwner) return null;

        //2. 프리셋 상세 조회
        List<BadgePresetDetail> presetDetails = badgePresetMapper.findAllByPresetId(presetId);
        if (presetDetails.isEmpty()) return null;

        //3. 현재 착용중인 뱃지 해제
        personalBadgeMapper.unwearAllBadgesByUserId(userId);

        //4. 프리셋의 personal_badge_id를 착용
        List<Long> wornBadgeIds = new ArrayList<>();
        for (BadgePresetDetail detail : presetDetails) {
            personalBadgeMapper.updateIsWornByPersonalBadgeId(detail.getPersonalBadgeId(), true);
            Long badgeId = personalBadgeMapper.findBadgeIdByPersonalBadgeId(detail.getPersonalBadgeId());
            wornBadgeIds.add(badgeId);
        }
        return BadgeResponseDto.ApplyBadgePresetResultDto.from(userId, wornBadgeIds);
    }

}
