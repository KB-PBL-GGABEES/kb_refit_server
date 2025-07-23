package org.refit.spring.hospital.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.hospital.dto.HospitalExpenseDetailResponseDto;
import org.refit.spring.hospital.dto.HospitalExpenseResponseDto;
import org.refit.spring.hospital.dto.InsuranceSubscribedResponseDto;
import org.refit.spring.mapper.HospitalMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalMapper hospitalMapper;

    public List<HospitalExpenseResponseDto>  findHospitalExpenseById(Long userId) {
        return hospitalMapper.findByAllHospitalExpenseByUserId(userId);
    }

    public HospitalExpenseDetailResponseDto findHospitalExpenseDetailById(Long receiptId) {
        return hospitalMapper.findByHospitalExpenseDetailId(receiptId);
    }

    public List<InsuranceSubscribedResponseDto> findInsuranceSubscribeById(Long hospitalSubscribeId) {
        return hospitalMapper.findByInsuranceSubscribeId(hospitalSubscribeId);
    }
}
