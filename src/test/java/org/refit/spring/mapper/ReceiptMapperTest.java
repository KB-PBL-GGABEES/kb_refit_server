package org.refit.spring.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refit.spring.config.RootConfig;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
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

    /*
    @DisplayName("영수증 목록을 조회합니다.")
    @Test
    void getList() {
        for (int i = 1; i <= 25; i++) {
            Receipt receipt = new Receipt();
            receipt.setCompanyId(1L);
            receipt.setUserId(1L);
            receipt.setTotalPrice(10000L + i);
            receipt.setSupplyPrice(9000L + i);
            receipt.setSurtax(1000L + i);
            receipt.setTransactionType("카드결제");
            receipt.setCreatedAt(new Date());
            receipt.setUpdatedAt(new Date());
            receipt.setCardId(1L);
            receiptMapper.create(receipt);
        }
        List<Receipt> list = receiptMapper.getList(Long.MAX_VALUE, 1L);
        assertNotNull(list);
        assertEquals(20, list.size());

        long prevId = Long.MAX_VALUE;
        for (Receipt receipt : list) {
            assertTrue(receipt.getReceiptId() < 9999L);
            assertTrue(receipt.getReceiptId() < prevId);
            prevId = receipt.getReceiptId();
        }
    }

     */

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
        Long total = receiptMapper.getTotal(1L);
        assertNotNull(total);
    }

    @DisplayName("저번 달 사용한 총 금액을 계산합니다.")
    @Test
    void getLastMonthTotal() {
        Long total = receiptMapper.getLastMonthTotal(1L);
        assertNotNull(total);
    }
}