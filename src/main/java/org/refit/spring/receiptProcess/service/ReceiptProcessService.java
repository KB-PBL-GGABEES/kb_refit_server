package org.refit.spring.receiptProcess.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.mapper.ReceiptProcessMapper;
import org.refit.spring.receiptProcess.dto.CheckCompanyResponseDto;
import org.refit.spring.receiptProcess.dto.ReceiptProcessRequestDto;
import org.refit.spring.receiptProcess.dto.ReceiptSelectDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptProcessService {

    private final ReceiptProcessMapper receiptProcessMapper;

    // 사업장 선택 조회
    public List<ReceiptSelectDto> getCompanySelectionListByUserId(Long userId) {
        return receiptProcessMapper.findCompanySelectionListByUserId(userId);
    }

    // 영수 처리 정보 조회
    public Long getCompanyIdByNameAndAddress(String companyName, String address) {
        return receiptProcessMapper.findCompanyIdByNameAndAddress(companyName, address);
    }

    // 사업자 정보 확인 요청
    public void registerVerifiedCompany(CheckCompanyResponseDto dto) {
        receiptProcessMapper.insertVerifiedCompany(dto);
    }

    // 영수 처리 요청
    public void registerReceiptProcess(ReceiptProcessRequestDto dto) {
        receiptProcessMapper.insertReceiptProcess(dto);
    }
}