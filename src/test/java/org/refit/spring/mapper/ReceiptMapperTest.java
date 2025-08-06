package org.refit.spring.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.refit.spring.config.RootConfig;
import org.refit.spring.receipt.dto.RejectedReceiptDto;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig(classes = {RootConfig.class})
@Transactional
class ReceiptMapperTest {

    @Autowired
    private ReceiptMapper receiptMapper;

    @DisplayName("새로운 영수증을 생성합니다.")
    @Test
    void create() {
        Receipt receipt = new Receipt();
        receipt.setTotalPrice(10000L);
        receipt.setSupplyPrice(9091L);
        receipt.setSurtax(909L);
        receipt.setTransactionType("카드결제");
        receipt.setCompanyId(1L);
        receipt.setUserId(1L);
        receipt.setCreatedAt(new Date());
        receipt.setUpdatedAt(new Date());
        receipt.setCardId(1L);
        receipt.setCompanyId(2018168693L);

        receiptMapper.create(receipt);

        assertNotNull(receipt.getReceiptId());
    }

    @DisplayName("새로운 영수증 항목을 생성합니다.")
    @Test
    void createReceiptContent() {
        Receipt receipt = new Receipt();
        receipt.setTotalPrice(10000L);
        receipt.setSupplyPrice(9091L);
        receipt.setSurtax(909L);
        receipt.setTransactionType("카드결제");
        receipt.setCompanyId(1L);
        receipt.setUserId(1L);
        receipt.setCreatedAt(new Date());
        receipt.setUpdatedAt(new Date());
        receipt.setCardId(1L);
        receipt.setCompanyId(2018168693L);
        receiptMapper.create(receipt);
        ReceiptContent content = new ReceiptContent();
        content.setReceiptId(receipt.getReceiptId());
        content.setAmount(0L);
        content.setMerchandiseId(1L);
        content.setCreatedAt(new Date());

        receiptMapper.createReceiptContent(content);

        assertNotNull(content.getReceiptContentId());
    }

    @DisplayName("영수증 항목을 업데이트합니다.")
    @Test
    void update() {
        Receipt receipt = new Receipt();
        receipt.setTotalPrice(10000L);
        receipt.setSupplyPrice(9091L);
        receipt.setSurtax(909L);
        receipt.setTransactionType("CARD");
        receipt.setCompanyId(1L);
        receipt.setUserId(1L);
        receipt.setCompanyId(2018168693L);
        receipt.setCreatedAt(new Date());
        receipt.setUpdatedAt(new Date());
        receipt.setCardId(1L);
        receiptMapper.create(receipt);

        receipt.setTotalPrice(15000L);
        receipt.setSupplyPrice(13636L);
        receipt.setSurtax(1364L);
        receipt.setUpdatedAt(new Date());

        receiptMapper.update(receipt.getUserId(), receipt);

        Receipt updated = receiptMapper.get(1L, receipt.getReceiptId());

        assertEquals(Long.valueOf(15000L), updated.getTotalPrice());
        assertEquals(Long.valueOf(13636L), updated.getSupplyPrice());
        assertEquals(Long.valueOf(1364L), updated.getSurtax());
    }

    @DisplayName("영수증 목록을 조회합니다.")
    @Test
    void getFilteredList() {
        for (int i = 1; i <= 25; i++) {
            Receipt receipt = new Receipt();
            receipt.setUserId(1L);
            receipt.setTotalPrice(10000L + i);
            receipt.setSupplyPrice(9000L + i);
            receipt.setSurtax(1000L + i);
            receipt.setTransactionType("카드결제");
            receipt.setCreatedAt(new Date());
            receipt.setUpdatedAt(new Date());
            receipt.setCardId(1L);
            receipt.setCompanyId(2018168693L);
            receiptMapper.create(receipt);
        }
        List<Receipt> list = receiptMapper.getFilteredList(1L, null, null, null, null, null, null, null);
        assertNotNull(list);

        long prevId = Long.MAX_VALUE;
        for (Receipt receipt : list) {
            assertTrue(receipt.getReceiptId() < 9999L);
            assertTrue(receipt.getReceiptId() < prevId);
            prevId = receipt.getReceiptId();
        }
    }

    @DisplayName("영수증 아이디를 바탕으로 영수증 항목을 찾습니다.")
    @Test
    void findContentsByReceiptId() {
        Receipt receipt = new Receipt();
        receipt.setTotalPrice(10000L);
        receipt.setSupplyPrice(9000L);
        receipt.setSurtax(1000L);
        receipt.setTransactionType("카드결제");
        receipt.setCompanyId(1L);
        receipt.setUserId(1L);
        receipt.setCreatedAt(new Date());
        receipt.setUpdatedAt(new Date());
        receipt.setCardId(1L);
        receipt.setCompanyId(2018168693L);
        receiptMapper.create(receipt);
        long merchandiseId = 1L;
        for (int i = 0; i < 3; i++) {
            ReceiptContent content = new ReceiptContent();
            content.setAmount((long) (1 + i));
            content.setReceiptId(receipt.getReceiptId());
            content.setMerchandiseId(merchandiseId);
            content.setCreatedAt(new Date());
            receiptMapper.createReceiptContent(content);
        }
        List<ReceiptContent> contents = receiptMapper.findContentsByReceiptId(1L, receipt.getReceiptId());

        assertNotNull(contents);
        assertEquals(3, contents.size());

        for (ReceiptContent content: contents) {
            assertEquals(receipt.getReceiptId(), content.getReceiptId());
        }
    }

    @DisplayName("영수증의 세부 사항을 조회합니다.")
    @Test
    void get() {
        Receipt receipt = new Receipt();
        receipt.setTotalPrice(10000L);
        receipt.setSupplyPrice(9000L);
        receipt.setSurtax(1000L);
        receipt.setTransactionType("카드결제");
        receipt.setCompanyId(1L);
        receipt.setUserId(1L);
        receipt.setCreatedAt(new Date());
        receipt.setUpdatedAt(new Date());
        receipt.setCardId(1L);
        receipt.setCompanyId(2018168693L);
        receiptMapper.create(receipt);
        Receipt result = receiptMapper.get(receipt.getUserId(), receipt.getReceiptId());
        assertNotNull(result);
        assertEquals(receipt.getReceiptId(), result.getReceiptId());
        assertEquals(Long.valueOf(10000L), result.getTotalPrice());
        assertEquals(Long.valueOf(9000L), result.getSupplyPrice());
        assertEquals(Long.valueOf(1000L), result.getSurtax());
        assertEquals("카드결제", result.getTransactionType());
    }

    @DisplayName("이번 달 사용한 총 금액을 계산합니다.")
    @Test
    void getTotal() {
        Long total = receiptMapper.getThisMonthTotal(1L);
        assertNotNull(total);
    }

    @DisplayName("저번 달 사용한 총 금액을 계산합니다.")
    @Test
    void getLastMonthTotal() {
        Long total = receiptMapper.getLastMonthTotal(1L);
        assertNotNull(total);
    }

    @DisplayName("회사 이름을 조회합니다.")
    @Test
    void getCompanyName() {
        Long companyId = 2018168693L;

        String companyName = receiptMapper.getCompanyName(companyId);

        assertNotNull(companyName);
        assertEquals("서울삼성병원", companyName);
    }

    @DisplayName("회사 주소를 조회합니다.")
    @Test
    void getCompanyAddress() {
        Long companyId = 2018168693L;

        String companyAddress = receiptMapper.getCompanyAddress(companyId);

        assertNotNull(companyAddress);
        assertEquals("서울특별시 광진구 화양동 4-3 403호", companyAddress);
    }

    @DisplayName("처리 상태를 조회합니다.")
    @Test
    void getState() {
        Long receiptId = 77L;

        String state = receiptMapper.getState(receiptId);
        assertNotNull(state);
        assertEquals("none", state);
    }

    @DisplayName("카드 번호를 조회합니다.")
    @Test
    void getCardNumber() {
        String number = receiptMapper.getCardNumber(5L, 1L);
        assertNotNull(number);
        assertEquals("12345678910", number);
    }

    @DisplayName("법인 카드 여부를 조회합니다.")
    @Test
    void getCorporate() {
        Integer is = receiptMapper.getCorporate(5L, 1L);
        assertNotNull(is);
        assertEquals(0, is);
    }

    @DisplayName("반려된 영수증을 조회합니다.")
    @Test
    void findRejected() {
        List<RejectedReceiptDto> list = receiptMapper.findRejected(1L);
        assertNotNull(list);
    }

    @DisplayName("배지를 찾습니다. 단, 존재하지 않을 때만")
    @Test
    void findBadge() {
        Long badge = receiptMapper.findBadge(5L, 391L);
        assertNotNull(badge);
        assertEquals(1L, badge);
    }

    @DisplayName("카테고리를 찾습니다.")
    @Test
    void findCategory() {
        Long category = receiptMapper.findCategory(5L, 391L);
        assertNotNull(category);
        assertEquals(1, category);
    }

    @DisplayName("대표 이름을 조회합니다.")
    @Test
    void findCeoName() {
        String ceoName = receiptMapper.findCeoName(2018168693L);
        assertNotNull(ceoName);
        assertEquals("이환주", ceoName);
    }
}