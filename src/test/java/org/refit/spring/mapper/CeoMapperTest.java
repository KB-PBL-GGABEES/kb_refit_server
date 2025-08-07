package org.refit.spring.mapper;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refit.spring.ceo.dto.CorporateCardDto;
import org.refit.spring.ceo.dto.ReceiptProcessApplicantDto;
import org.refit.spring.ceo.entity.Ceo;
import org.refit.spring.ceo.enums.RejectState;
import org.refit.spring.ceo.enums.Sort;
import org.refit.spring.config.RootConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringJUnitWebConfig(classes = {RootConfig.class})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class })
@Log4j
class CeoMapperTest {

    @Autowired
    private CeoMapper ceoMapper;

    // SQL 연결, Mapper 설정, 외래키 연동 정상 확인
    @Test
    @DisplayName("insertProcess - 영수 처리 항목 추가")
    @Transactional  // 트랜잭션 안에서 실행
    @Rollback(true) // 테스트 트랜잭션 자동 롤백
    void insertProcess() {

        Long ceoId = 1L;
        Long userId = 1L;
        Long receiptId = 1L;

        ceoMapper.insertProcess(ceoId, userId, receiptId);
        log.info("InsertProcess - 실행결과 : receiptId = " + receiptId);
    }

    // ceo_id가 정상 조회 확인
    @Test
    @DisplayName("findCeoId - company_id로 ceo_id 조회")
    void findCeoId() {
        Long companyId = 1L;
        Long ceoId = ceoMapper.findCeoId(companyId);

        if(ceoId != null) {
            log.info("findCeoId - 실행 결과 : ceoId = " + ceoId);
        } else {
            log.info("ceo_id는 null이 아니어야 함");
        }
    }

    @Test
    @DisplayName("getPendingReceipts - 경비 처리가 필요한 내역 조회")
    void getPendingReceipts() {
        Long userId = 1L;
        List<Ceo> list = ceoMapper.getPendingReceipts(userId);

        if (list.isEmpty()) {
            log.info("getPendingReceipts - 리스트가 비어 있음");
        } else {
            log.info("getPendingReceipts - 조회 결과 : " + list.size() + "건");
            list.forEach(dto -> log.info(dto));
        }
    }

    @Test
    @DisplayName("countCompleteReceiptsThisMonth - 이번 달 처리 완료된 항목 개수 조회")
    void countCompletedReceiptsThisMonth() {
        Long userId = 1L;
        int count = ceoMapper.countCompletedReceiptsThisMonth(userId);

        log.info("countCompleteReceiptThisMonth - 실행 결과 : count = " + count);
    }

    @Test
    @DisplayName("getReceiptList - 경비 청구 항목 상세 조회")
    void getReceiptProcessDetail() {
        Long receiptId = 1L;

        ReceiptProcessApplicantDto dto = ceoMapper.getReceiptProcessDetail(receiptId);

        if(dto != null) {
            log.info("getReceiptList - 조회 결과\n" + dto);
        } else {
            log.info("getReceiptList - 조회 실패, dto가 null");
        }
    }

    @Test
    @DisplayName("커서 기반 경비 완료 내역 조회")
    void getCompletedReceipts() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);
        params.put("cursorId", Long.MAX_VALUE);
        params.put("sort", Sort.Newest);
        params.put("size", 10L);
        params.put("state", null);
        params.put("period", null);
        params.put("startDate", null);
        params.put("endDate", null);

        List<Ceo> list = ceoMapper.getCompletedReceipts(params);

        log.info("조회 결과 수 : " + list.size());
        list.forEach(c -> log.info("receiptId = " + c.getReceiptId()));

        assertThat(list).isNotNull();
    }

    @Test
    @DisplayName("updateProcessState - 영수 처리 상태 업데이트")
    @Transactional
    @Rollback(true)
    void updateProcessState() {
        Long receiptProcessId = 1L;
        String processState = "rejected";
        String rejectedReason = "반려 사유";

        ceoMapper.updateProcessState(receiptProcessId, processState, rejectedReason);
        log.info("updateProcessState - 실행 결과 : processState = " + processState);
    }

    @Test
    @DisplayName("getCorporateCardCostThisMonth - 이번 달 법카 사용 총액")
    void getCorporateCardCostThisMonth() {
        Long ceoId = 1L;
        Long totalCost = ceoMapper.getCorporateCardCostThisMonth(ceoId);

        if(totalCost != null) {
            log.info("getCorporateCardCostThisMonth - 실행 결과 : " + totalCost);
        } else {
            log.info("getCorporateCardCostThisMonth - 실행 결과 : 0");
        }
    }

    @Test
    @DisplayName("getCorporateCardCostLastMonth - 지난 달 법카 사용 총액")
    void getCorporateCardCostLastMonth() {
        Long ceoId = 1L;
        Long totalCost = ceoMapper.getCorporateCardCostLastMonth(ceoId);

        if(totalCost != null) {
            log.info("getCorporateCardCostLastMonth - 실행 결과 : " + totalCost);
        } else {
            log.info("getCorporateCardCostLastMonth - 실행 결과 : 0");
        }
    }

    @Test
    @DisplayName("커서 기반 법카 내역 조회")
    void getCorporateCardReceipts() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);
        params.put("cursorId", Long.MAX_VALUE);
        params.put("sort", Sort.Newest);
        params.put("size", 10L);
        params.put("state", null);
        params.put("period", null);
        params.put("startDate", null);
        params.put("endDate", null);

        List<CorporateCardDto> list = ceoMapper.getCorporateCardReceipts(params);

        log.info("법카 조회 수 : " + list.size());
        list.forEach(c -> log.info("receiptId = " + c.getReceiptId()));

        assertThat(list).isNotNull();
    }
}