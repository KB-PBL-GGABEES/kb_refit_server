package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface ReceiptMapper {
    @Insert("INSERT INTO receipt (total_price, supply_price, surtax, transaction_type, is_refund, created_at, updated_at) VALUES (#{totalPrice}, #{supplyPrice}, #{surtax}, #{transactionType}, #{isRefund}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "receiptId")
    void create(Receipt receipt);

    @Insert("INSERT INTO receiptContent (amount, receipt_id, merchandise_id, created_at) VALUES (#{amount}, #{receiptId}, #{merchandiseId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "receiptContentId")
    void createReceiptContent(ReceiptContent receiptContent);

    @Update("UPDATE receipt SET total_price = #{totalPrice}, supply_price = #{supplyPrice}, surtax = #{surtax} WHERE receipt_id = #{receiptId}")
    void update(Receipt receipt);

    @Select("SELECT * FROM receipt WHERE receipt_id < #{cursorId} ORDER BY receipt_id DESC LIMIT 20")
    List<Receipt> getList(@Param("userId") Long userId, @Param("cursorId") Long cursorId);

    @Select("SELECT * FROM receiptContent WHERE receipt_id = #{receiptId}")
    List<ReceiptContent> findContentsByReceiptId(@Param("receiptId") Long receiptId);

    @Select("SELECT * FROM receipt WHERE receipt_id = #{receiptId}")
    Receipt get(Long id);

    @Select("SELECT SUM(totalPrice) FROM receipt WHERE (is_refund = 0) AND (created_at BETWEEN DATE_ADD(NOW(), INTERVAL -1 MONTH) AND NOW())")
    Long getTotal();
}
