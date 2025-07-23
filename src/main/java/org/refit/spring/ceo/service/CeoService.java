package org.refit.spring.ceo.service;

import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;

import java.util.List;

public interface CeoService {

    // 경비 처리가 필요한 내역 조회
    List<CeoListDto> getListUndone();

    // 경비 청구 항목 상세 조회
    ReceiptDetailDto getReceiptDetail(Long receipted);

    // 경비 처리 완료 내역 조회
    List<CeoListDto> getListDone(int period);

    // 처리 완료된 항목 이메일 전송

    // 영수 처리 승인
    
    // 영수 처리 반려
    
    // 한달 법카 금액 조회
    
    // 법카 내역 조회

    // 페이지네이션

}
