package org.refit.spring.hospital.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.hospital.dto.*;
import org.refit.spring.hospital.enums.HospitalSort;
import org.refit.spring.mapper.HospitalMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalMapper hospitalMapper;


    private void validateRequiredFields(Map<String, Object> fields) {
        List<String> missing = new ArrayList<>();


        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Object value = entry.getValue();
            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                missing.add(entry.getKey());
            }
        }

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("다음 필수 항목이 누락되었거나 비어 있습니다: " + String.join(", ", missing));
        }
    }

    // 의료 영수증 목록 조회
    @Transactional(readOnly = true)
    public MedicalReceiptListCursorDto getFilteredList(Long userId, MedicalListRequestDto medicalListRequestDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        long paginationSize = (medicalListRequestDto.getSize() != null && medicalListRequestDto.getSize() > 0) ? medicalListRequestDto.getSize() : 20;

        // 1. 기본 정렬값 지정
        if (medicalListRequestDto.getSort() == null) medicalListRequestDto.setSort(HospitalSort.LATEST);

        // 2. 커서 초기화
        if (medicalListRequestDto.getCursorId() == null) {
            medicalListRequestDto.setCursorId((medicalListRequestDto.getSort() == HospitalSort.OLDEST) ? 0L : Long.MAX_VALUE);
        }

        // 3. 날짜 필터
        if (medicalListRequestDto.getPeriod() == null) {
            params.put("startDate", medicalListRequestDto.getStartDate());
            params.put("endDate", medicalListRequestDto.getEndDate());
        } else {
            params.put("period",  medicalListRequestDto.getPeriod());
        }

        params.put("cursorId", medicalListRequestDto.getCursorId());
        params.put("sort", medicalListRequestDto.getSort());
        params.put("filter", medicalListRequestDto.getFilter());
        params.put("type", medicalListRequestDto.getType());
        params.put("size", paginationSize);
        validateRequiredFields(params);

        List<MedicalReceiptDto> list = hospitalMapper.getFilteredList(params);

        // 커서 아이디 초기화
        Long nextCursorId = (list.size() < paginationSize) ?  null : list.get(list.size() - 1).getReceiptId();

        return MedicalReceiptListCursorDto.from(list, nextCursorId);
    }


    // 의료 영수증 상세 조회
    public MedicalReceiptDetailDto findHospitalExpenseDetail(Long userId, Long receiptId) {
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("userId", userId);
        requiredFields.put("receiptId", receiptId);

        validateRequiredFields(requiredFields);

        List<MedicalReceiptDetailDto> results =
                hospitalMapper.findHospitalExpenseDetailByUserIdAndReceiptId(userId, receiptId);

        if (results == null || results.isEmpty()) return null;

        return results.get(0);
    }

    // 진료비 세부산정내역 PDF 파일명 DB저장
    @Transactional
    public void updateHospitalVoucher(Long userId, MedicalImageFileNameDownloadDto dto) {
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("userId", userId);
        requiredFields.put("receiptId", dto.getReceiptId());
        requiredFields.put("medicalImageFileName", dto.getMedicalImageFileName());

        validateRequiredFields(requiredFields);

        String state = hospitalMapper.findProcessStateByReceiptId(dto.getReceiptId());

        if (state == null) {
            hospitalMapper.insertEmptyHospitalProcess(dto.getReceiptId(), "");
        }
        hospitalMapper.updateHospitalVoucher(dto.getReceiptId(), dto.getMedicalImageFileName(), userId);
    }


    // 진료비 세부산정내역 PDF 파일명 조회
    public MedicalImageFileNameCheckDto findHospitalVoucher(Long userId, Long receiptId) {
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("userId", userId);
        requiredFields.put("receiptId", receiptId);

        validateRequiredFields(requiredFields);


        String fileName = hospitalMapper.findHospitalVoucherByReceiptId(receiptId, userId);
        if (fileName == null || fileName.isEmpty()) return null;
        return new MedicalImageFileNameCheckDto(fileName);
    }

    // 최근 병원비 조회
    public MedicalReceiptRecentDto getHospitalRecentInfo(Long userId) {
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("userId", userId);

        validateRequiredFields(requiredFields);

        MedicalReceiptRecentDto dto =  new MedicalReceiptRecentDto();

        boolean exists = hospitalMapper.existsUserReceipt(userId);
        if (!exists) {
            dto.setInsuranceBillable(0L);
            dto.setRecentTotalPrice(0L);
            return dto;
        }
        return hospitalMapper.findByHospitalRecentId(userId);
    }

    // 가입된 보험 목록 조회
    public List<InsuranceSubscribedCheckDto> findInsuranceSubscribeById(Long userId) {
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("userId", userId);
        validateRequiredFields(requiredFields);

        List<InsuranceSubscribedCheckDto> result = hospitalMapper.findByInsuranceSubscribeId(userId);
        if (result == null || result.isEmpty()) {
            return null;
        }

        return result;
    }


    // 보험 청구_방문 정보
    public MedicalCheckDto getHospitalVisitInfo(Long userId, Long receiptId) {
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("userId", userId);
        requiredFields.put("receiptId", receiptId);
        validateRequiredFields(requiredFields);

        return hospitalMapper.findHospitalVisitInfo(userId, receiptId);
    }

    // 보험 청구_PATCH
    public void insertInsuranceClaim(InsuranceClaimDto dto, Long userId) {
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





