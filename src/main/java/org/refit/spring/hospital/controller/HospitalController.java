package org.refit.spring.hospital.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.hospital.dto.*;
import org.refit.spring.hospital.enums.HospitalFilter;
import org.refit.spring.hospital.enums.HospitalSort;
import org.refit.spring.hospital.enums.HospitalType;
import org.refit.spring.hospital.service.HospitalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "의료 영수증 API", description = "의료 영수증 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital")
public class HospitalController {

    private final HospitalService hospitalService;

    // 병원 영수증 목록 조회 (커서 기반 페이지네이션)

    @ApiOperation(value = "의료비 납입 내역 조회", notes = "의료비 납입 내역을 조회할 수 있습니다.")
    @GetMapping("/list")
    public ResponseEntity<?> getHospitalExpenses(@ApiIgnore @UserId Long userId,
                                                 @RequestParam(value = "cursorId", required = false) Long cursorId) {
        HospitalListDto dto = hospitalService.getHospitalExpenses(userId, cursorId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "최근 N개월 내 의료비 납입 내역 조회", notes = "최근 N개월 내의 병원 영수증을 조회할 수 있습니다.")
    @GetMapping("/list/months")
    public ResponseEntity<?> getHospitalExpensesWithinMonths(@ApiIgnore @UserId Long userId,
                                                             @RequestParam(value = "cursorId", required = false) Long cursorId,
                                                             @RequestParam(value = "period") Integer period) {
        HospitalListDto dto = hospitalService.getListMonths(userId, cursorId, period);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "기간 필터로 의료비 납입 내역 조회", notes = "시작일과 종료일 사이의 병원 영수증 목록을 조회합니다.")
    @GetMapping("/list/period")
    public ResponseEntity<?> getHospitalExpensesByPeriod(@ApiIgnore @UserId Long userId,
                                                         @RequestParam(value = "cursorId", required = false) Long cursorId,
                                                         @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                         @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        HospitalListDto dto = hospitalService.getListPeriod(userId, cursorId, startDate, endDate);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "필터 기반 병원 영수증 조회", notes = "필터(기간, 종류, 정렬, 청구 여부)에 따라 병원 영수증을 조회합니다.")
    @GetMapping("/list/filter")
    public ResponseEntity<?> getHospitalExpensesWithFilter(
            @ApiIgnore @UserId Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer period,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) HospitalType type,
            @RequestParam(required = false) HospitalSort sort,
            @RequestParam(required = false) HospitalFilter filter) {

        System.out.println("=== [Controller] cursorId: " + cursorId + " / sort: " + sort);

        HospitalListDto dto = hospitalService.getFilteredList(
                userId, cursorId, period, startDate, endDate, type, filter, sort
        );

        return ResponseEntity.ok(dto);
    }


    // 병원 영수증 상세 조회
    @ApiOperation(value = "의료비 납입 내역 상세 조회", notes = "의료비 납입 내역을 상세 조회할 수 있습니다.")
    @GetMapping("/detail")
    public ResponseEntity<?> getHospitalExpenseDetail(@ApiIgnore @UserId Long userId,
            @RequestParam("receiptId") Long receiptId)
    {

        HospitalExpenseDetailResponseDto result = hospitalService.findHospitalExpenseDetail(userId, receiptId);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "해당 userId와 receiptId에 해당하는 데이터가 없습니다."));
        }

        return ResponseEntity.ok(result);
    }

    // 진료비 세부산정내역 PDF 파일명 DB저장
    @ApiOperation(value = "진료비 세부산정내역 파일명 저장", notes = "프론트에서 보낸 파일명을 DB에 저장합니다.")
    @PostMapping("/voucher")
    public ResponseEntity<?> saveHospitalVoucher(@ApiIgnore @UserId Long userId,
                                                 @RequestBody HospitalVoucherRequestDto dto) {

        if (dto.getReceiptId() == null || dto.getHospitalVoucher() == null || dto.getHospitalVoucher().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "필수 정보 누락"));
        }

        hospitalService.updateHospitalVoucher(userId, dto);
        return ResponseEntity.ok(Collections.singletonMap("message", "파일명이 정상적으로 저장되었습니다."));
    }



    // 진료비 세부산정내역 PDF 파일명 조회
    @ApiOperation(value = "진료비 세부산정내역 파일명 조회", notes = "receiptId에 해당하는 PDF 파일명을 반환합니다.")
    @GetMapping("/voucher")
    public ResponseEntity<?> getHospitalVoucher(@ApiIgnore @UserId Long userId,
                                                @RequestParam("receiptId") Long receiptId) {

        HospitalVoucherResponseDto result = hospitalService.findHospitalVoucher(userId, receiptId);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "해당 영수증에 대한 파일명이 존재하지 않습니다."));
        }

        return ResponseEntity.ok(result);
    }

    // 최근 병원비 조회
    @ApiOperation(value = "최근 병원비 및 보험청구 가능 건수", notes = "최근 3년간 병원비 총액과 보험청구 가능한 건수를 조회합니다.")
    @GetMapping("/recent")
    public ResponseEntity<?> getHospitalRecentInfo(@ApiIgnore @UserId Long userId) {
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
    public ResponseEntity<?> findInsuranceSubscribeById(@ApiIgnore @UserId Long userId) {
        List<InsuranceSubscribedResponseDto> result = hospitalService.findInsuranceSubscribeById(userId);

        if (result == null || result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "가입된 보험 정보가 없습니다."));
        }

        return ResponseEntity.ok(result);
    }

    // 보험 청구_방문 정보
    @ApiOperation(value = "보험 청구 요청-방문 정보 조회", notes = "보험 청구 페이지에서 병원 방문 정보 조회를 할 수 있습니다.")
    @GetMapping("/insurance/claim")
    public ResponseEntity<?> getHospitalVisitInfo(
            @ApiIgnore @UserId Long userId,
            @RequestParam Long receiptId) {

        InsuranceVisit result = hospitalService.getHospitalVisitInfo(userId, receiptId);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "해당 영수증 정보를 찾을 수 없습니다."));
        }

        return ResponseEntity.ok(result);
    }
    // 보험 청구_PATCH
    @ApiOperation(value = "보험 청구 요청", notes = "보험 청구 페이지에서 보험 청구를 요청할 수 있습니다.")
    @PatchMapping("/insurance/claim")
    public ResponseEntity<?> claimInsurance(@ApiIgnore @UserId Long userId,
                                            @RequestBody InsuranceClaimRequestDto dto) {
        try {
            hospitalService.insertInsuranceClaim(dto, userId);

            Map<String, String> successMap = new HashMap<>();
            successMap.put("message", "보험 청구가 완료되었습니다.");
            return ResponseEntity.ok(successMap);

        } catch (IllegalArgumentException e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }
    }
}

