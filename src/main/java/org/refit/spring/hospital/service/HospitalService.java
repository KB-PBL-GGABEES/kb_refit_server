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

    public List<HospitalExpenseResponseDto> getHospitalExpenses(Long userId, Date cursorDate) {
        if (cursorDate == null) {
            return hospitalMapper.findFirstPage(userId);
        }
        return hospitalMapper.findByCursorDate(userId, cursorDate);
    }

    public HospitalExpenseDetailResponseDto findHospitalExpenseDetail(Long userId, Long receiptId) {
        return hospitalMapper.findHospitalExpenseDetailByUserIdAndReceiptId(userId, receiptId);
    }

    public HospitalRecentResponseDto getHospitalRecentInfo(Long userId) {
        return hospitalMapper.findByHospitalRecentId(userId);
    }

    public List<InsuranceSubscribedResponseDto> findInsuranceSubscribeById(Long hospitalSubscribeId) {
        return hospitalMapper.findByInsuranceSubscribeId(hospitalSubscribeId);
    }
}
