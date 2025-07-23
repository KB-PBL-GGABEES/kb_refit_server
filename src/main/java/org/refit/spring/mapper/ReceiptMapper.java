package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;

import java.util.List;

@Mapper
public interface ReceiptMapper {
    @Insert("INSERT INTO receipt (total_price, supply_price, surtax, transaction_type, created_at, updated_at, company_id, user_id) VALUES (#{totalPrice}, #{supplyPrice}, #{surtax}, #{transactionType}, NOW(), NOW(), #{companyId}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "receiptId")
    void create(Receipt receipt);

    @Insert("INSERT INTO receipt_content (amount, receipt_id, merchandise_id, created_at) VALUES (#{amount}, #{receiptId}, #{merchandiseId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "receiptContentId")
    void createReceiptContent(ReceiptContent receiptContent);

    @Update("UPDATE receipt SET total_price = #{totalPrice}, supply_price = #{supplyPrice}, surtax = #{surtax} WHERE receipt_id = #{receiptId}")
    void update(Receipt receipt);

    @Select("SELECT * FROM receipt WHERE receipt_id < #{cursorId} ORDER BY receipt_id DESC LIMIT 20")
    List<Receipt> getList(@Param("cursorId") Long cursorId);

    @Select("SELECT * FROM receipt_content WHERE receipt_id = #{receiptId}")
    List<ReceiptContent> findContentsByReceiptId(@Param("receiptId") Long receiptId);

    @Select("SELECT * FROM receipt WHERE receipt_id = #{receiptId}")
    Receipt get(Long id);

    @Select("SELECT SUM(total_price) FROM receipt WHERE MONTH(created_at) = MONTH(CURRENT_DATE()) AND YEAR(created_at) = YEAR(CURRENT_DATE())")
    Long getTotal();

    @Select("SELECT SUM(total_price) FROM receipt WHERE MONTH(created_at) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) AND YEAR(created_at) = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH))")
    Long getLastMonthTotal();
}
