package org.refit.spring.mapper;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refit.spring.ceo.domain.CeoVO;
import org.refit.spring.config.RootConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class })
@Log4j
class CeoMapperTest {

    @Autowired
    private CeoMapper ceoMapper;

    @Test
    @DisplayName("경비 처리가 필요한 내역 조회")
    void getListUndone() {
        List<CeoVO> list = ceoMapper.getListUndone();

        if (list.isEmpty()) {
            log.info("경비 처리가 필요한 내역 조회 테스트 (리스트는 비어 있음)");
        } else {
            log.info("경비 처리가 필요한 내역 조회 테스트 (조회 결과: " + list.size() + "건)");
            list.forEach(dto -> log.info(dto));
        }
    }
}