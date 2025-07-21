package org.refit.spring.ceo.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.ceo.dto.CeoListDTO;
import org.refit.spring.mapper.CeoMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CeoServiceImpl implements CeoService {
    private final CeoMapper ceoMapper;

    // 경비 처리가 필요한 내역 조회
    @Override
    public List<CeoListDTO> getListUndone() {
        return ceoMapper.getListUndone()
                .stream()
                .map(CeoListDTO::of)
                .collect(Collectors.toList());
    }
}
