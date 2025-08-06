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

import java.util.*;

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
            return ResponseEntity.ok(companyList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "회사 목록 조회 중 오류가 발생했습니다."));
        }
    }

    @ApiOperation(value = "영수 처리 정보 조회", notes = "영수 처리 정보를 조회할 수 있습니다.")
    @GetMapping
    public ResponseEntity<?> getCompanyInfo(@RequestParam("companyId") Long companyId) {
        try {
            ReceiptProcessCheckDto companyInfo = receiptProcessService.getCompanyInfoByCompanyId(companyId);
            return ResponseEntity.ok(companyInfo);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "회사 정보 확인 중 오류가 발생했습니다."));
        }
    }

    @ApiOperation(value = "사업자 진위확인 및 직원 등록", notes = "사업자번호를 OpenAPI로 확인 후 유효하면 직원으로 등록")
    @PostMapping("/checkCompany")
    public ResponseEntity<?> verifyCompany(@ApiIgnore @UserId Long userId,
                                           @RequestBody CheckCompanyRequestDto dto) {
        try {
            CheckCompanyResponseDto result = receiptProcessService.verifyAndRegisterEmployee(dto, userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
          catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            log.error("사업자 진위확인 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "진위확인 처리 중 서버 오류가 발생했습니다."));
        }
    }

    @ApiOperation(value = "영수 처리 요청", notes = "영수 처리를 요청할 수 있습니다.")
    @PatchMapping
    public ResponseEntity<?> upsertReceiptProcess(@ApiIgnore @UserId Long userId,
                                                  @RequestBody ReceiptProcessRequestDto dto) {
        try {
            receiptProcessService.upsertReceiptProcess(dto, userId);
            return ResponseEntity.noContent().build();

        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "서버 오류로 인해 영수처리에 실패했습니다."));

        }
    }
}