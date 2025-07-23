package org.refit.spring.ceo.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;
import org.refit.spring.mapper.CeoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CeoServiceImpl implements CeoService {
    private final CeoMapper ceoMapper;

    // 경비 처리가 필요한 내역 조회
    @Override
    public List<CeoListDto> getListUndone() {
        return ceoMapper.getListUndone()
                .stream()
                .map(CeoListDto::of)
                .collect(Collectors.toList());
    }

    // 경비 청구 항목 상세 조회
    @Override
    public ReceiptDetailDto getReceiptDetail(Long receipted) {
        return ceoMapper.getReceiptDetail(receipted);
    }

    // 경비 처리 완료 내역 조회
    @Override
    public List<CeoListDto> getListDone(int period) {
        LocalDateTime fromDate = LocalDateTime.now().minusMonths(period);
        return ceoMapper.getListDone(fromDate)
                .stream()
                .map(CeoListDto::of)
                .collect(Collectors.toList());
    }

    // 처리 완료된 항목 이메일 전송

    // 영수 처리 승인

    // 영수 처리 반려

    // 한달 법카 금액 조회

    // 법카 내역 조회

    // 페이지네이션
}
