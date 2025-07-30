package org.refit.spring.wallet.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.entity.User;
import org.refit.spring.mapper.*;
import org.refit.spring.wallet.dto.BadgeRequestDto;
import org.refit.spring.wallet.dto.BadgeResponseDto;
import org.refit.spring.wallet.dto.WalletResponseDto;
import org.refit.spring.wallet.entity.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    public BadgeResponseDto.BadgeListDto getBadgeList(Long userId) {
        List<Badge> allBadges = badgeMapper.findAll(); // 전체 뱃지 목록
        List<Long> ownedBadgeIds = personalBadgeMapper.findBadgeIdsByUserId(userId); // 보유한 뱃지 ID

        Set<Long> ownedSet = ownedBadgeIds.stream().collect(Collectors.toSet());

        List<BadgeResponseDto.BadgeListDetailDto> badgeList = allBadges.stream()
                .map(badge -> BadgeResponseDto.BadgeListDetailDto.from(badge, ownedSet.contains(badge.getBadgeId())))
                .collect(Collectors.toList());

        return BadgeResponseDto.BadgeListDto.from(badgeList);
    }


    public BadgeResponseDto.wornBadgeListAndBenefitDto getWornBadgeListAndBenefit(Long userId) {
        List<PersonalBadge> wornBadges = personalBadgeMapper.findWornBadgesByUserId(userId);

        List<BadgeResponseDto.BadgeDetailDto> badgeDetailDtoList = wornBadges.stream()
                .map(pb -> {
                    Badge badge = badgeMapper.findById(pb.getBadgeId());
                    return BadgeResponseDto.BadgeDetailDto.from(badge, pb);
                })
                .collect(Collectors.toList());

        return BadgeResponseDto.wornBadgeListAndBenefitDto.from(badgeDetailDtoList);
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

        // 2. 전체 브랜드 리스트
        List<WalletBrand> walletBrandList = walletBrandMapper.findAllWalletBrands();

        // 3. 유저가 보유한 walletId 리스트
        List<Long> ownedWalletIds = personalWalletBrandMapper.findOwnedWalletIdsByUserId(userId);

        // 4. DTO로 변환
        List<WalletResponseDto.WalletBrandDto> walletBrandDtos = walletBrandList.stream()
                .map(wallet -> WalletResponseDto.WalletBrandDto.from(wallet, ownedWalletIds.contains(wallet.getWalletId())))
                .collect(Collectors.toList());

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
//        boolean isMounted = (isOwned && personal.isMounted());

        return WalletResponseDto.WalletBrandDetailDto.from(brand, user, personal != null ? personal : new PersonalWalletBrand(), isOwned);
    }

    public WalletResponseDto.ToggleMountedWalletDto toggleMountedWallet(Long userId, Long walletId) {
        PersonalWalletBrand target = personalWalletBrandMapper.findByUserIdAndWalletId(userId, walletId);
        if (target == null) {
            return null; // ← 이게 핵심!
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

        // 2. 각 프리셋에 대해 디테일 정보 가져오기 및 변환
        return badgePresets.stream()
                .map(preset -> {
                    // 해당 preset에 포함된 personalBadgeId들 조회
                    List<BadgePresetDetail> details = badgePresetMapper.findAllByPresetId(preset.getPresetId());

                    // DTO로 변환
                    List<BadgeResponseDto.BadgePresetDto> badgeDtoList = details.stream()
                            .map(BadgeResponseDto.BadgePresetDto::from)
                            .collect(Collectors.toList());

                    // 최종 응답 DTO로 조립
                    return BadgeResponseDto.BadgePresetListDto.from(preset, badgeDtoList);
                })
                .collect(Collectors.toList());
    }

}
