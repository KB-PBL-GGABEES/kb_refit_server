package org.refit.spring.hospital.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.refit.spring.hospital.dto.HospitalExpenseDetailResponseDto;
import org.refit.spring.hospital.dto.HospitalExpenseResponseDto;
import org.refit.spring.hospital.dto.HospitalRecentResponseDto;
import org.refit.spring.hospital.dto.InsuranceSubscribedResponseDto;
import org.refit.spring.hospital.service.HospitalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital")
public class HospitalController {

    private final HospitalService hospitalService;

    // 병원 영수증 목록 조회 (커서 기반 페이지네이션)
    @ApiOperation(value = "의료비 납입 내역 조회", notes = "의료비 납입 내역을 조회할 수 있습니다.")
    @GetMapping("/list")
    public ResponseEntity<?> getHospitalExpenses(
            @UserId Long userId,
            @RequestParam(value = "cursorDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date cursorDate) {

        List<HospitalExpenseResponseDto> list = hospitalService.getHospitalExpenses(userId, cursorDate);

        if (list == null || list.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "해당 유저의 병원 영수증 데이터가 없습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        // 다음 커서 계산
        Date nextCursor = (list.size() < 20) ? null : list.get(list.size() - 1).getCreatedAt();
        String nextCursorDateStr = (nextCursor != null) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextCursor) : null;

        Map<String, Object> response = new HashMap<>();
        response.put("data", list);
        response.put("nextCursorDate", nextCursorDateStr);

        return ResponseEntity.ok(response);
    }

    // 병원 영수증 상세 조회
    @ApiOperation(value = "의료비 납입 내역 상세 조회", notes = "의료비 납입 내역을 상세 조회할 수 있습니다.")
    @GetMapping("/detail")
    public ResponseEntity<?> getHospitalExpenseDetail(
            @UserId Long userId,
            @RequestParam("receiptId") Long receiptId) {

        HospitalExpenseDetailResponseDto result = hospitalService.findHospitalExpenseDetail(userId, receiptId);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "해당 userId와 receiptId에 해당하는 데이터가 없습니다."));
        }

        return ResponseEntity.ok(result);
    }

    // 최근 병원비 조회
    @ApiOperation(value = "최근 병원비 및 보험청구 가능 건수", notes = "최근 3년간 병원비 총액과 보험청구 가능한 건수를 조회합니다.")
    @GetMapping("/recent")
    public ResponseE3ntity<?> getHospitalRecentInfo(@UserId Long userId) {
        HospitalRecentResponseDto result = hospitalService.getHospitalRecentInfo(userId);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "해당 userId에 대한 최근 병원비 데이터가 없습니다."));
        }

        return ResponseEntity.ok(result);
    }

    // 가입된 보험 목록 조회
    @ApiOperation(value = "가입된 보험 목록 조회", notes = "가입된 보험 목록을 조회할 수 있습니다.")
    @GetMapping("/insurance")
    public ResponseEntity<?> findInsuranceSubscribeById(@UserId Long userId) {
        List<InsuranceSubscribedResponseDto> result = hospitalService.findInsuranceSubscribeById(userId);

        if (result == null || result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "해당 userId의 데이터가 존재하지 않습니다."));
        }

        return ResponseEntity.ok(result);
    }
}