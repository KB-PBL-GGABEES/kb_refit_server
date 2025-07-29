package org.refit.spring.hospital.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.hospital.dto.HospitalExpenseDetailResponseDto;
import org.refit.spring.hospital.dto.HospitalExpenseResponseDto;
import org.refit.spring.hospital.dto.HospitalRecentResponseDto;
import org.refit.spring.hospital.dto.InsuranceSubscribedResponseDto;
import org.refit.spring.mapper.HospitalMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalMapper hospitalMapper;

    // 병원 영수증 목록 조회
    public List<HospitalExpenseResponseDto> getHospitalExpenses(Long userId, Date cursorDate) {
        if (cursorDate == null) {
            return hospitalMapper.findFirstPage(userId);
        }
        return hospitalMapper.findByCursorDate(userId, cursorDate);
    }

    // 병원 영수증 상세 조회
    public HospitalExpenseDetailResponseDto findHospitalExpenseDetail(Long userId, Long receiptId) {
        List<HospitalExpenseDetailResponseDto> results =
                hospitalMapper.findHospitalExpenseDetailByUserIdAndReceiptId(userId, receiptId);

        if (results == null || results.isEmpty()) return null;

        return results.get(0);
    }

    // 최근 병원비 조회
    public HospitalRecentResponseDto getHospitalRecentInfo(Long userId) {
        boolean exists = hospitalMapper.existsUserReceipt(userId);
        if (!exists) {
            return null; // 컨트롤러에서 에러 응답 처리
        }
        return hospitalMapper.findByHospitalRecentId(userId);
    }

    // 가입된 보험 목록 조회
    public List<InsuranceSubscribedResponseDto> findInsuranceSubscribeById(Long hospitalSubscribeId) {
        return hospitalMapper.findByInsuranceSubscribeId(hospitalSubscribeId);
    }


//    // 보험 청구 요청
//    public boolean requestInsuranceClaim(Long receiptId, Date sickedDate, String visitedReason, Long insuranceId, String processState) {
//        int updatedRows = hospitalMapper.updateInsuranceClaimRequest(
//                receiptId, sickedDate, visitedReason, insuranceId, processState);
//        return updatedRows > 0;
//    }
}





