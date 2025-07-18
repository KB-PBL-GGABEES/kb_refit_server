package org.refit.spring.merchandise.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.mapper.MerchandiseMapper;
import org.refit.spring.merchandise.entity.Merchandise;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchandiseService {
    private final MerchandiseMapper merchandiseMapper;

    public Merchandise findByMerchandiseId(Long merchandiseId) {
        return merchandiseMapper.findByMerchandiseId(merchandiseId);
    }
}
