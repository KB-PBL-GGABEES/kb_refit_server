package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;

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
}
