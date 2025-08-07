package org.refit.spring.receipt.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.entity.User;
import org.refit.spring.mapper.*;
import org.refit.spring.merchandise.entity.Merchandise;
import org.refit.spring.receipt.dto.*;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;
import org.refit.spring.receipt.enums.ReceiptFilter;
import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.receipt.enums.ReceiptType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import javax.swing.text.html.Option;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptMapper receiptMapper;
    private final MerchandiseMapper merchandiseMapper;
    private final CeoMapper ceoMapper;
    private final PersonalBadgeMapper personalBadgeMapper;
    private final UserMapper userMapper;
    private final HospitalMapper hospitalMapper;
    private final ReceiptProcessMapper receiptProcessMapper;

    private final DataSource dataSource;

    public void validateRequiredFields(Map<String, Object> fields) {
        List<String> missing = new ArrayList<>();

        for (Map.Entry<String, Object> entry: fields.entrySet()) {
            Object value = entry.getValue();
            if (value == null) missing.add(entry.getKey());
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("다음 필수 항목이 누락되었거나 비어 있습니다: " + String.join(", ", missing));
        }
    }

    @Transactional
    public Receipt create(ReceiptRequestDto dto, Long userId) throws ParseException {
        List<ReceiptContentRequestsDto> requestList = dto.getContentsList();
        Merchandise firstMerchandise = merchandiseMapper.findByMerchandiseId(requestList.get(0).getMerchandiseId());
        Receipt receipt = initReceipt(dto.getCardId(), firstMerchandise.getCompanyId(), receiptMapper.getCompanyName(firstMerchandise.getCompanyId()), userId);
        Long category = receiptMapper.findCategory(userId, receipt.getReceiptId());
        if (category == 1) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Long price = firstMerchandise.getMerchandisePrice();
            receipt.setTotalPrice(price);
            Long supply = (long) (price / 1.1);
            receipt.setSupplyPrice(supply);
            receipt.setSurtax(price - supply);
            receipt.setUpdatedAt(new Date());
            User user = userMapper.findByUserId(userId);
            hospitalMapper.insertEmptyHospitalProcess(receipt.getReceiptId(), user.getName() + "_" + sdf.format(receipt.getCreatedAt()) + "_이비인후과");
        }
        else {
            List<ReceiptContentDetailDto> list = makeContents(dto.getContentsList(), receipt);
            receipt.setContentList(list);
            ceoMapper.insertProcess(null, userId, receipt.getReceiptId());
        }
        updatePrice(receipt);
        receiptMapper.update(userId, receipt);
        return receipt;
    }


    @Transactional
    public Receipt refund(Long userId, Long receiptId) {
        Receipt nowReceipt = receiptMapper.get(userId, receiptId);
        if (nowReceipt == null) throw new IllegalArgumentException("존재하지 않는 영수증입니다.");
        if (nowReceipt.getTotalPrice() <= 0) throw new IllegalArgumentException("이미 환불된 영수증입니다.");
        Receipt refundReceipt = new Receipt();
        refundReceipt.setTotalPrice(-nowReceipt.getTotalPrice());
        refundReceipt.setSupplyPrice(-nowReceipt.getSupplyPrice());
        refundReceipt.setSurtax(-nowReceipt.getSurtax());
        refundReceipt.setTransactionType("환불");
        refundReceipt.setCreatedAt(new Date());
        refundReceipt.setUpdatedAt(new Date());
        refundReceipt.setCompanyId(nowReceipt.getCompanyId());
        refundReceipt.setCompanyName(receiptMapper.getCompanyName(nowReceipt.getCompanyId()));
        refundReceipt.setUserId(userId);
        refundReceipt.setCardId(nowReceipt.getCardId());
        receiptMapper.create(refundReceipt);

        List<ReceiptContent> originalContents = receiptMapper.findContentsByReceiptId(userId, receiptId);
        List<ReceiptContentDetailDto> refundContentList = new ArrayList<>();
        for (ReceiptContent content: originalContents) {
            ReceiptContent refundContent = new ReceiptContent();
            refundContent.setAmount(-content.getAmount());
            refundContent.setMerchandiseId(content.getMerchandiseId());
            refundContent.setReceiptId(refundReceipt.getReceiptId());
            refundContent.setCreatedAt(new Date());
            receiptMapper.createReceiptContent(refundContent);

            Merchandise merchandise = merchandiseMapper.findByMerchandiseId(content.getMerchandiseId());
            ReceiptContentDetailDto dto = new ReceiptContentDetailDto();
            dto.setMerchandiseId(merchandise.getMerchandiseId());
            dto.setMerchandiseName(merchandise.getMerchandiseName());
            dto.setMerchandisePrice(merchandise.getMerchandisePrice());
            dto.setAmount(-content.getAmount());

            refundContentList.add(dto);
        }
        refundReceipt.setContentList(refundContentList);

        return refundReceipt;
    }

    private Receipt initReceipt(Long cardId, Long companyId, String companyName, Long userId) {
        Receipt receipt = new Receipt();
        receipt.setTotalPrice(0L);
        receipt.setSupplyPrice(0L);
        receipt.setSurtax(0L);
        receipt.setCardId(cardId);
        receipt.setTransactionType("카드 결제");
        receipt.setCreatedAt(new Date());
        receipt.setUpdatedAt(new Date());
        receipt.setUserId(userId);
        receipt.setCompanyId(companyId);
        receipt.setCompanyName(companyName);
        receiptMapper.create(receipt);
        return receipt;
    }

    private List<ReceiptContentDetailDto> makeContents(List<ReceiptContentRequestsDto> list, Receipt receipt) {
        List<ReceiptContentDetailDto> contentDtoList = new ArrayList<>();
        for (ReceiptContentRequestsDto requestsDto: list) {
            Merchandise merchandise = merchandiseMapper.findByMerchandiseId(requestsDto.getMerchandiseId());
            ReceiptContent content = makeContent(requestsDto, merchandise, receipt.getReceiptId());
            receiptMapper.createReceiptContent(content);

            contentDtoList.add(makeMerchandise(merchandise, requestsDto));
            receipt.setTotalPrice(receipt.getTotalPrice() + merchandise.getMerchandisePrice() * requestsDto.getAmount());
        }
        return contentDtoList;
    }

    private ReceiptContent makeContent(ReceiptContentRequestsDto dto, Merchandise merchandise, Long receiptId) {
        ReceiptContent content = new ReceiptContent();
        content.setAmount(dto.getAmount());
        content.setReceiptId(receiptId);
        content.setCreatedAt(new Date());
        content.setMerchandiseId(merchandise.getMerchandiseId());
        return content;
    }

    private ReceiptContentDetailDto makeMerchandise(Merchandise merchandise, ReceiptContentRequestsDto dto) {
        ReceiptContentDetailDto result = new ReceiptContentDetailDto();
        result.setMerchandiseId(merchandise.getMerchandiseId());
        result.setMerchandiseName(merchandise.getMerchandiseName());
        result.setMerchandisePrice(merchandise.getMerchandisePrice());
        result.setAmount(dto.getAmount());
        return result;
    }

    private void updatePrice(Receipt receipt) {
        long total = receipt.getTotalPrice();
        long supply = (long) (total / 1.1);
        receipt.setSupplyPrice(supply);
        receipt.setSurtax(total - supply);
        receipt.setUpdatedAt(new Date());
        User user = userMapper.findByUserId(receipt.getUserId());
        if (personalBadgeMapper.checkIsWorn(receipt.getUserId(), receipt.getReceiptId())) {
            user.setTotalStarPoint((long) (user.getTotalStarPoint() + receipt.getTotalPrice() * 0.05));
        }
        user.setTotalCarbonPoint(user.getTotalCarbonPoint() + 100);
    }

    @Transactional(readOnly = true)
    public ReceiptListCursorDto getFilteredList(Long userId, ReceiptListRequestDto receiptListRequestDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        long paginationSize = (receiptListRequestDto.getSize() != null && receiptListRequestDto.getSize() > 0) ? receiptListRequestDto.getSize() : 20;

        params.put("size", paginationSize);
        if (receiptListRequestDto.getSort() == null) receiptListRequestDto.setSort(ReceiptSort.LATEST);

        if (receiptListRequestDto.getCursorId() == null) {
            receiptListRequestDto.setCursorId((receiptListRequestDto.getSort() == ReceiptSort.OLDEST) ? 0L : Long.MAX_VALUE);
        }
        if (receiptListRequestDto.getFilter() == null) receiptListRequestDto.setFilter(ReceiptFilter.ALL);

        if (receiptListRequestDto.getPeriod() == null) {
            params.put("startDate", receiptListRequestDto.getStartDate());
            params.put("endDate", receiptListRequestDto.getEndDate());
        } else {
            params.put("period",receiptListRequestDto.getPeriod());
        }

        if (receiptListRequestDto.getType() == null) receiptListRequestDto.setType(ReceiptType.ALL);

        params.put("cursorId", receiptListRequestDto.getCursorId());
        params.put("sort", receiptListRequestDto.getSort());
        params.put("filter", receiptListRequestDto.getFilter());
        params.put("type", receiptListRequestDto.getType());

        validateRequiredFields(params);


        List<Receipt> list = receiptMapper.getFilteredList(params);

        // 커서 아이디 초기화
        Long nextCursorId = (list.size() < paginationSize) ?  null : list.get(list.size() - 1).getReceiptId();
        return ReceiptListCursorDto.from(list, nextCursorId);
    }


    @Transactional(readOnly = true)
    public ReceiptDetailDto get(Long userId, Long cursorId, Long receiptId) {
        if (cursorId == null) cursorId = Long.MAX_VALUE;

        Receipt receipt = receiptMapper.get(userId, receiptId);
        if (receipt == null) {
            throw new NoSuchElementException();
        }
        List<ReceiptContent> contents = receiptMapper.findContentsByReceiptId(userId, receiptId);
        List<ReceiptContentDetailDto> contentDtoList = new ArrayList<>();
        for (ReceiptContent content: contents) {
            Merchandise merchandise = merchandiseMapper.findByMerchandiseId(content.getMerchandiseId());
            ReceiptContentDetailDto dto = new ReceiptContentDetailDto();
            dto.setMerchandiseId(merchandise.getMerchandiseId());
            dto.setMerchandiseName(merchandise.getMerchandiseName());
            dto.setMerchandisePrice(merchandise.getMerchandisePrice());
            dto.setAmount(content.getAmount());
            contentDtoList.add(dto);
        }
        return new ReceiptDetailDto(
                receipt.getUserId(),
                receipt.getReceiptId(),
                receipt.getCompanyId(),
                receiptMapper.getCompanyName(receipt.getCompanyId()),
                receiptMapper.findCeoName(receipt.getCompanyId()),
                receiptMapper.getCompanyAddress(receipt.getCompanyId()),
                contentDtoList,
                receipt.getTotalPrice(),
                receipt.getSupplyPrice(),
                receipt.getSurtax(),
                receipt.getTransactionType(),
                receipt.getCreatedAt(),
                Optional.ofNullable(receiptMapper.getState(receiptId)).orElse("none"),
                receiptMapper.getCardNumber(userId, receipt.getCardId()),
                Optional.ofNullable(receiptMapper.getCorporate(userId, receipt.getCardId())).orElse(0),
                Optional.ofNullable(receiptProcessMapper.findReason(receiptId)).orElse("")
        );
    }


    @Transactional
    public MonthlyExpenseDto getTotal(Long userId) {
        MonthlyExpenseDto dto = new MonthlyExpenseDto();
        dto.setUserId(userId);
        dto.setThisMonthExpense(receiptMapper.getThisMonthTotal(userId));
        dto.setLastMonthExpense(receiptMapper.getLastMonthTotal(userId));
        return dto;
    }

    public RejectedReceiptListDto getRejected(Long userId) {
        List<RejectedReceiptDto> rejectedList = receiptMapper.findRejected(userId);
        for (RejectedReceiptDto dto: rejectedList) {
            List<ReceiptContent> contents = receiptMapper.findContentsByReceiptId(userId, dto.getReceiptId());
            List<ReceiptContentDetailDto> contentDtoList = new ArrayList<>();
            for (ReceiptContent content: contents) {
                Merchandise merchandise = merchandiseMapper.findByMerchandiseId(content.getMerchandiseId());
                ReceiptContentDetailDto contentDto = new ReceiptContentDetailDto();
                contentDto.setMerchandiseId(content.getMerchandiseId());
                contentDto.setAmount(content.getAmount());
                contentDto.setMerchandiseName(merchandise.getMerchandiseName());
                contentDto.setMerchandisePrice(merchandise.getMerchandisePrice());

                contentDtoList.add(contentDto);
            }
            dto.setContentList(contentDtoList);
        }
        return new RejectedReceiptListDto(rejectedList);
    }

    public void changeState(Long userId, Long receiptId) {
        int update = receiptMapper.updateProcessState(userId, receiptId);
        if (update == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 영수증은 처리 대상이 아닙니다.");
    }

    public void checkAndInsertBadge(Long userId, Long receiptId) {
        Long badgeId = receiptMapper.findBadge(userId, receiptId);
        if (badgeId != null) {
            String sqlTemplate = personalBadgeMapper.getCondition(badgeId);
            String sql = sqlTemplate.replace("${userId}", userId.toString());
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    long res = rs.getLong(1);
                    if (res > 0) personalBadgeMapper.insertBadge(badgeId, userId);
                }
            } catch (SQLException e) {}
        }
    }
}
