package org.refit.spring.receiptProcess.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.receiptProcess.dto.*;
import org.refit.spring.receiptProcess.service.ReceiptProcessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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

    @ApiOperation(value = "사업자 진위확인 및 직원 등록", notes = "사업자번호를 OpenAPI로 확인 후 유효하면 직원으로 등록")
    @PostMapping("/checkCompany")
    public ResponseEntity<?> verifyCompany(@ApiIgnore @UserId Long userId,
                                           @RequestBody CheckCompanyRequestDto dto) {
        try {
            if (dto.getCompanyId() == null || dto.getCeoName() == null || dto.getOpenedDate() == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "필수 정보(companyId, ceoName, openedDate)가 누락되었습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            CheckCompanyResponseDto result = receiptProcessService.verifyAndRegisterEmployee(dto, userId);
            if (!result.isValid()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "유효하지 않은 사업자번호입니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("사업자 진위확인 중 오류", e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "진위확인 처리 중 서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @ApiOperation(value = "영수 처리 요청", notes = "영수 처리를 요청할 수 있습니다.")
    @PatchMapping
    public ResponseEntity<?> upsertReceiptProcess(@ApiIgnore @UserId Long userId,
                                                  @RequestBody ReceiptProcessRequestDto dto) {
        try {
            // 유효성 검사
            if (dto.getReceiptId() == null ||
                    dto.getProgressType() == null || dto.getProgressType().trim().isEmpty() ||
                    dto.getProgressDetail() == null || dto.getProgressDetail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("message", "receiptId, progressType, progressDetail는 필수 입력 항목입니다."));
            }

            // 비어 있는 voucher는 null 처리
            if (dto.getVoucher() != null && dto.getVoucher().trim().isEmpty()) {
                dto.setVoucher(null);
            }

            // 서비스 호출
            receiptProcessService.upsertReceiptProcess(dto, userId);

            return ResponseEntity.ok(Collections.singletonMap("message", "영수처리 요청이 완료되었습니다."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "서버 오류로 인해 영수처리에 실패했습니다."));
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