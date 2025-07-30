package org.refit.spring.pos.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.mapper.MerchandiseMapper;
import org.refit.spring.merchandise.entity.Merchandise;
import org.refit.spring.pos.dto.PosResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PosService {
    private final MerchandiseMapper merchandiseMapper;

    public PosResponseDto.GetMerchandiseListDto getMerchandiseList(Long companyId) {
        List<Merchandise> merchandiseList = merchandiseMapper.findAllByCompanyId(companyId);

        if (merchandiseList == null || merchandiseList.isEmpty()) {
            return null;
        }

        List<PosResponseDto.GetMerchandiseDto> dtoList = merchandiseList.stream()
                .map(PosResponseDto.GetMerchandiseDto::from)
                .collect(Collectors.toList());

        return PosResponseDto.GetMerchandiseListDto.from(dtoList, companyId);
    }
}
