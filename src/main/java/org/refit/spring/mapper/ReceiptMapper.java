package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.receipt.provider.ReceiptQueryProvider;
import org.refit.spring.receipt.dto.RejectedReceiptDto;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReceiptMapper {
    @Insert("INSERT INTO receipt (total_price, supply_price, surtax, transaction_type, created_at, updated_at, company_id, user_id, card_id) VALUES (#{totalPrice}, #{supplyPrice}, #{surtax}, #{transactionType}, #{createdAt}, #{updatedAt}, #{companyId}, #{userId}, #{cardId})")
    @Options(useGeneratedKeys = true, keyProperty = "receiptId")
    void create(Receipt receipt);

    @Insert("INSERT INTO receipt_content (amount, receipt_id, merchandise_id, created_at) VALUES (#{amount}, #{receiptId}, #{merchandiseId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "receiptContentId")
    void createReceiptContent(ReceiptContent receiptContent);

    @Update("UPDATE receipt SET total_price = #{receipt.totalPrice}, supply_price = #{receipt.supplyPrice}, surtax = #{receipt.surtax} WHERE user_id = #{userId} AND receipt_id = #{receipt.receiptId}")
    void update(@Param("userId") Long userId, @Param("receipt") Receipt receipt);

    @SelectProvider(type = ReceiptQueryProvider.class, method = "buildFilteredQuery")
    List<Receipt> getFilteredList(Map<String, Object> params);

    @Select("SELECT * FROM receipt_content rc JOIN receipt r ON rc.receipt_id = r.receipt_id WHERE r.user_id = #{userId} AND rc.receipt_id = #{receiptId}")
    List<ReceiptContent> findContentsByReceiptId(@Param("userId") Long userId, @Param("receiptId") Long receiptId);

    @Select("SELECT * FROM receipt WHERE user_id = #{userId} AND receipt_id = #{receiptId}")
    Receipt get(@Param("userId") Long userId, @Param("receiptId") Long receiptId);

    @Select("SELECT IFNULL(SUM(total_price), 0) FROM receipt WHERE user_id = #{userId} AND MONTH(created_at) = MONTH(CURRENT_DATE()) AND YEAR(created_at) = YEAR(CURRENT_DATE())")
    Long getThisMonthTotal(@Param("userId") Long userId);

    @Select("SELECT IFNULL(SUM(total_price), 0) FROM receipt WHERE user_id = #{userId} AND MONTH(created_at) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) AND YEAR(created_at) = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH))")
    Long getLastMonthTotal(@Param("userId") Long userId);

    @Select("SELECT company_name FROM company WHERE company_id = #{companyId}")
    String getCompanyName(@Param("companyId") Long companyId);

    @Select("SELECT address FROM company WHERE company_id = #{companyId}")
    String getCompanyAddress(@Param("companyId") Long companyId);

    @Select("SELECT process_state FROM receipt_process WHERE receipt_id = #{receiptId}")
    String getState(@Param("receiptId") Long receiptId);

    @Select("SELECT card_number FROM card WHERE user_id = #{userId} AND card_id = #{cardId}")
    String getCardNumber(@Param("userId") Long userId, @Param("cardId") Long cardId);

    @Select("SELECT is_corporate FROM card WHERE user_id = #{userId} AND card_id = #{cardId}")
    Integer getCorporate(@Param("userId") Long userId, @Param("cardId") Long cardId);

    @Select("SELECT r.*, co.company_name AS company_name, rp.process_state, rp.receipt_process_id FROM receipt_process rp " +
            "INNER JOIN receipt r ON rp.receipt_id = r.receipt_id " +
            "INNER JOIN company co ON co.company_id = r.company_id " +
            "INNER JOIN card c  ON r.card_id = c.card_id " +
            "INNER JOIN user u ON c.user_id = u.user_id " +
            "WHERE c.user_id = #{userId} AND c.is_corporate = 1 AND rp.process_state = 'rejected' " +
            "ORDER BY rp.created_at ASC")
    List<RejectedReceiptDto> findRejected(@Param("userId") Long userId);

    @Update("UPDATE receipt_process rp SET rp.process_state = 'deposit' " +
            "WHERE rp.receipt_id = #{receiptId} " +
            "AND EXISTS (SELECT 1 FROM receipt r " +
            "INNER JOIN card c ON r.card_id = c.card_id " +
            "WHERE c.user_id = #{userId} " +
            "AND r.receipt_id = rp.receipt_id " +
            "AND rp.process_state = 'rejected')")
    Integer updateProcessState(@Param("userId") Long userId,
                            @Param("receiptId") Long receiptId);

    @Select("SELECT b.badge_id FROM receipt r " +
            "INNER JOIN company c ON r.company_id = c.company_id " +
            "INNER JOIN categories ca ON c.category_id = ca.category_id " +
            "INNER JOIN badge b ON ca.category_id = b.category_id " +
            "LEFT OUTER JOIN personal_badge p ON b.badge_id = p.badge_id AND p.user_id = #{userId} " +
            "WHERE r.receipt_id = #{receiptId} AND p.badge_id IS NULL")
    Long findBadge(@Param("userId") Long userId, @Param("receiptId") Long receiptId);

    @Select("SELECT ca.category_id FROM receipt r INNER JOIN company c ON r.company_id = c.company_id " +
            "INNER JOIN categories ca ON c.category_id = ca.category_id " +
            "WHERE r.receipt_id = #{receiptId}")
    Long findCategory(@Param("userId") Long userId, @Param("receiptId") Long receiptId);

    @Select("SELECT ceo_name FROM company WHERE company_id = #{companyId}")
    String findCeoName(@Param("companyId") Long companyId);

    @Select("SELECT user_id FROM card WHERE card_id = #{cardId}")
    Long findCardId(@Param("cardId") Long cardId);

    @Select("SELECT * FROM receipt r " +
            "INNER JOIN company c ON r.company_id = c.company_id " +
            "WHERE r.company_id = #{companyId} " +
            "ORDER BY r.created_at DESC")
    List<Receipt> findReceiptByCompanyId(@Param("companyId") Long companyId);
}
