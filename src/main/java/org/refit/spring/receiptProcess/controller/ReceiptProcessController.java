package org.refit.spring.receiptProcess.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.receiptProcess.dto.*;
import org.refit.spring.receiptProcess.service.ReceiptProcessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collections;
import java.util.List;

@Api(tags = "영수 처리 API", description = "영수 처리 관련 API입니다.")
@RestController
@RequestMapping("/api/receipt/process")
@RequiredArgsConstructor
public class ReceiptProcessController {

    private final ReceiptProcessService receiptProcessService;


    @ApiOperation(value = "사업장 선택 조회", notes = "사업장을 조회할 수 있습니다.")
    @GetMapping("/select")
    public ResponseEntity<?> getCompanySelectionList(@ApiIgnore @UserId Long userId) {
        try {
            List<ReceiptSelectDto> companyList = receiptProcessService.getCompanySelectionListByUserId(userId);
            if (companyList == null || companyList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "조회된 회사 정보가 없습니다."));
            }
            return ResponseEntity.ok(companyList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "회사 목록 조회 중 오류가 발생했습니다."));
        }
    }

    @ApiOperation(value = "영수 처리 정보 조회", notes = "영수 처리 정보를 조회할 수 있습니다.")
    @GetMapping
    public ResponseEntity<?> getCompanyInfo(@RequestParam("receiptId") Long receiptId) {
        try {
            ReceiptProcessCheckDto companyInfo = receiptProcessService.getCompanyInfoByReceiptId(receiptId);
            if (companyInfo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "해당 영수증에 대한 회사 정보가 존재하지 않습니다."));
            }
            return ResponseEntity.ok(companyInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "회사 정보 확인 중 오류가 발생했습니다."));
        }
    }

    @ApiOperation(value = "사업자 정보 확인 요청", notes = "사업자 정보 확인을 요청할 수 있습니다.")
    @PostMapping("/checkCompany")
    public ResponseEntity<?> registerVerifiedCompany(@RequestBody CheckCompanyResponseDto dto) {
        try {
            if (dto == null || dto.getCompanyId() == null) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("message", "필수 회사 정보가 누락되었습니다."));
            }
            receiptProcessService.registerVerifiedCompany(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("message", "회사 정보 등록 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "회사 정보 등록 중 오류가 발생했습니다."));
        }
    }


    @ApiOperation(value = "영수 처리 요청", notes = "영수 처리를 요청할 수 있습니다.")
    @PostMapping
    public ResponseEntity<?> registerReceiptProcess(@RequestBody ReceiptProcessRequestDto dto,
                                                    @ApiIgnore @UserId Long userId) {
        try {
            // 필수값 검증
            if (dto == null || dto.getReceiptId() == null || dto.getProgressType() == null) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("message", "필수 경비 처리 정보가 누락되었습니다."));
            }

            // voucher가 비어있으면 null로 세팅
            if (dto.getVoucher() == null || dto.getVoucher().trim().isEmpty()) {
                dto.setVoucher(null);
            }

            // userId → ceoId 변환
            Long ceoId = receiptProcessService.findCeoIdByUserIdAndReceiptId(userId, dto.getReceiptId());
            if (ceoId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "해당 사용자에 대한 ceo 정보가 존재하지 않습니다."));
            }

            // 등록 요청
            receiptProcessService.registerReceiptProcess(dto, ceoId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "경비 처리 등록 중 오류가 발생했습니다."));
        }
    }

    @ApiOperation(value = "관련 이미지 파일명 DB조회", notes = "관련 이미지 파일명을 조회할 수 있습니다.")
    @GetMapping("/voucher")
    public ResponseEntity<?> getReceiptVoucher(@RequestParam("receiptId") Long receiptId) {
        ReceiptVoucherResponseDto dto = receiptProcessService.getVoucherFileName(receiptId);

        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "해당 영수증의 이미지 파일명이 없습니다."));
        }

        return ResponseEntity.ok(dto);
    }
}