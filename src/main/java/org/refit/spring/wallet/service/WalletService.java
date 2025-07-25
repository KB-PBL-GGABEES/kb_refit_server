package org.refit.spring.wallet.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.mapper.BadgeMapper;
import org.refit.spring.mapper.PersonalBadgeMapper;
import org.refit.spring.wallet.dto.BadgeResponseDto;
import org.refit.spring.wallet.entity.Badge;
import org.refit.spring.wallet.entity.PersonalBadge;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final BadgeMapper badgeMapper;
    private final PersonalBadgeMapper personalBadgeMapper;

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
}
