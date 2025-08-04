package org.refit.spring.hospital.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.hospital.dto.*;
import org.refit.spring.hospital.enums.HospitalFilter;
import org.refit.spring.hospital.enums.HospitalSort;
import org.refit.spring.hospital.enums.HospitalType;
import org.refit.spring.mapper.HospitalMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalMapper hospitalMapper;

    // 의료 영수증 목록 조회
    @Transactional(readOnly = true)
    public MedicalReceiptListCursorDto getFilteredList(Long userId, Long cursorId, Integer period,
                                                       Date startDate, Date endDate,
                                                       HospitalType type, HospitalFilter filter, HospitalSort sort) {

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        // 1. 기본 정렬값 지정
        if (sort == null) sort = HospitalSort.LATEST;

        // 2. 커서 초기화
        if (cursorId == null) {
            cursorId = (sort == HospitalSort.OLDEST) ? 0L : Long.MAX_VALUE;
        }

        params.put("cursorId", cursorId);
        params.put("sort", sort); // 필수

        // 3. 날짜 필터
        if (period != null) {
            params.put("period", period); // 내부에서 INTERVAL #{period} MONTH 처리
        } else if (startDate != null && endDate != null) {
            params.put("startDate", startDate);
            params.put("endDate", endDate);
        }

        // 4. 기타 조건
        if (type != null) params.put("type", type);
        if (filter != null) params.put("filter", filter);

        // 디버깅 로그
        System.out.println("===[ 필터링 디버깅 ]===\nsort: " + sort + "\ncursorId: " + cursorId +
                "\nperiod: " + period + "\nstartDate: " + startDate + "\nendDate: " + endDate +
                "\ntype: " + type + "\nfilter: " + filter);

        // 5. 쿼리 실행
        List<MedicalReceiptListDto> list = hospitalMapper.getFilteredList(params);

        Long nextCursorId = (list.size() < 10) ? null : list.get(list.size() - 1).getReceiptId();
        return MedicalReceiptListCursorDto.from(list, nextCursorId);
    }

    // 2. 최근 n개월 내 의료 영수증 조회
    @Transactional(readOnly = true)
    public MedicalReceiptListCursorDto getListMonths(Long userId, Long cursorId, Integer period) {
        if (cursorId == null) cursorId = Long.MAX_VALUE;

        List<MedicalReceiptListDto> list = hospitalMapper.findByCursorIdWithinMonths(userId, cursorId, period);
        Long nextCursorId = (list.size() < 10) ? null : list.get(list.size() - 1).getReceiptId();

        return MedicalReceiptListCursorDto.from(list, nextCursorId);
    }

    // 3. 시작일 ~ 종료일 기간 필터로 의료 영수증 조회
    @Transactional(readOnly = true)
    public MedicalReceiptListCursorDto getListPeriod(Long userId, Long cursorId, Date startDate, Date endDate) {
        if (cursorId == null) cursorId = Long.MAX_VALUE;

        List<MedicalReceiptListDto> list = hospitalMapper.findByCursorIdWithPeriod(userId, cursorId, startDate, endDate);
        Long nextCursorId = (list.size() < 10) ? null : list.get(list.size() - 1).getReceiptId();

        return MedicalReceiptListCursorDto.from(list, nextCursorId);
    }





    // 의료 영수증 상세 조회
    public MedicalReceiptDetailDto findHospitalExpenseDetail(Long userId, Long receiptId) {
        List<MedicalReceiptDetailDto> results =
                hospitalMapper.findHospitalExpenseDetailByUserIdAndReceiptId(userId, receiptId);

        if (results == null || results.isEmpty()) return null;

        return results.get(0);
    }

    // 진료비 세부산정내역 PDF 파일명 DB저장
    @Transactional
    public void updateHospitalVoucher(Long userId, MedicalImageFileNameDownloadDto dto) {
        String state = hospitalMapper.findProcessStateByReceiptId(dto.getReceiptId());

        if (state == null) {
            hospitalMapper.insertEmptyHospitalProcess(dto.getReceiptId());
        }

        hospitalMapper.updateHospitalVoucher(dto.getReceiptId(), dto.getMedicalImageFileName(), userId);
    }


    // 진료비 세부산정내역 PDF 파일명 조회
    public MedicalImageFileNameCheckDto findHospitalVoucher(Long userId, Long receiptId) {
        String fileName = hospitalMapper.findHospitalVoucherByReceiptId(receiptId, userId);
        if (fileName == null || fileName.isEmpty()) return null;
        return new MedicalImageFileNameCheckDto(fileName);
    }

    // 최근 병원비 조회
    public MedicalReceiptRecentDto getHospitalRecentInfo(Long userId) {
        boolean exists = hospitalMapper.existsUserReceipt(userId);
        if (!exists) {
            return null; // 컨트롤러에서 에러 응답 처리
        }
        return hospitalMapper.findByHospitalRecentId(userId);
    }

    // 가입된 보험 목록 조회
    public List<InsuranceSubscribedCheckDto> findInsuranceSubscribeById(Long hospitalSubscribeId) {
        return hospitalMapper.findByInsuranceSubscribeId(hospitalSubscribeId);
    }


    // 보험 청구_방문 정보
    public MedicalCheckDto getHospitalVisitInfo(Long userId, Long receiptId) {
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





