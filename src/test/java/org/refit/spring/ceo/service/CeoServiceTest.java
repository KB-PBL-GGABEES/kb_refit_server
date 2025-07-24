package org.refit.spring.ceo.service;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.config.RootConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class} )
@Log4j
class CeoServiceTest {

    @Autowired
    CeoService ceoService;

    @Test
    void getListUndone() {
        List<CeoListDto> list = ceoService.getListUndone(null);

        if (list.isEmpty()) {
            log.info("경비 처리가 필요한 내역 조회 테스트 (리스트는 비어 있음)");
        } else {
            log.info("경비 처리가 필요한 내역 조회 테스트 (조회 결과: " + list.size() + "건)");
            list.forEach(dto -> log.info(dto));
        }
    }
}