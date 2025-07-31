package org.refit.spring.hospital.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.hospital.dto.*;
import org.refit.spring.mapper.HospitalMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    // 진료비 세부산정내역 PDF 파일명 DB저장
    public void updateHospitalVoucher(Long userId, HospitalVoucherRequestDto dto) {
        hospitalMapper.updateHospitalVoucher(dto.getReceiptId(), dto.getHospitalVoucher(), userId);
    }


    // 진료비 세부산정내역 PDF 파일명 조회
    public HospitalVoucherResponseDto findHospitalVoucher(Long userId, Long receiptId) {
        String fileName = hospitalMapper.findHospitalVoucherByReceiptId(receiptId, userId);
        if (fileName == null || fileName.isEmpty()) return null;
        return new HospitalVoucherResponseDto(fileName);
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


    // 보험 청구_방문 정보
    public InsuranceVisit getHospitalVisitInfo(Long userId, Long receiptId) {
        return hospitalMapper.findHospitalVisitInfo(userId, receiptId);
    }

    // 보험 청구_POST
    public void insertInsuranceClaim(InsuranceClaimRequestDto dto, Long userId) {
        // 1. 이미 hospital_process 존재하는지 확인
        String existingState = hospitalMapper.findProcessStateByReceiptId(dto.getReceiptId());

        if (existingState != null && !existingState.equalsIgnoreCase("none")) {
            throw new IllegalArgumentException("이미 보험 청구가 접수된 영수증입니다.");
        }

        // 2. 보험 가입 여부 확인
        boolean isSubscribed = hospitalMapper.existsUserInsurance(userId, dto.getInsuranceId());
        if (!isSubscribed) {
            throw new IllegalArgumentException("가입된 보험에 가입되어 있지 않습니다.");
        }

        // 3. 기본값 처리
        if (dto.getProcessState() == null || dto.getProcessState().trim().isEmpty()) {
            dto.setProcessState("inProgress");
        }
        // 4. INSERT or UPDATE
        if (existingState != null) {
            // 기존 hospital_process가 있지만 상태가 none이면 갱신
            hospitalMapper.updateInsuranceClaim(dto);
        } else {
            // 신규 청구
            hospitalMapper.insertInsuranceClaim(dto);
        }
    }
}





