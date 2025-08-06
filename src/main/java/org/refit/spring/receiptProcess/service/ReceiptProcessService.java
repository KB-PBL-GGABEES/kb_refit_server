package org.refit.spring.receiptProcess.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.refit.spring.mapper.ReceiptProcessMapper;
import org.refit.spring.receiptProcess.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptProcessService {

    // DB 연동용 Mapper
    private final ReceiptProcessMapper receiptProcessMapper;
    // 외부 OpenAPI 호출용 HTTP 클라이언트
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${openapi.api-key}")
    // application.properties에서 주입받는 OpenAPI 인증 키
    private String apiKey;

    @Value("${openapi.business-validate-url}")
    // OpenAPI 요청 URL
    private String validateUrl;

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

    public CheckCompanyResponseDto verifyAndRegisterEmployee(CheckCompanyRequestDto dto, Long userId) throws JsonProcessingException {
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("companyId", dto.getCompanyId());
        requiredFields.put("ceoName", dto.getCeoName());
        requiredFields.put("openedDate", dto.getOpenedDate());

        //필수 파라미터 validation
        validateRequiredFields(requiredFields);

        // 1. OpenAPI 요청
        String url = validateUrl + "?serviceKey=" + apiKey + "&returnType=JSON";

        Map<String, Object> business = new HashMap<>();
        business.put("b_no", String.valueOf(dto.getCompanyId()));
        business.put("start_dt", new SimpleDateFormat("yyyyMMdd").format(dto.getOpenedDate()));
        business.put("p_nm", dto.getCeoName());

        Map<String, Object> body = new HashMap<>();
        body.put("businesses", Collections.singletonList(business));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class
        );

        JsonNode root = objectMapper.readTree(response.getBody());
        String statusCode = root.path("status_code").asText(); // JsonNode → String
        switch (statusCode) {
            case "OK":
                break; // 계속 진행
            case "BAD_JSON_REQUEST":
                throw new IllegalArgumentException("요청 JSON 포맷이 잘못되었습니다. 사업자 정보를 다시 확인해주세요.");
            case "REQUEST_DATA_MALFORMED":
                throw new RuntimeException("필수 파라미터가 누락되었습니다. 관리자에게 문의해주세요.");
            case "TOO_LARGE_REQUEST":
                throw new IllegalArgumentException("요청 사업자 등록번호가 10개를 초과했습니다.");
            case "INTERNAL_ERROR":
                throw new RuntimeException("OpenAPI 서버 내부 오류가 발생했습니다.");
            case "HTTP_ERROR":
                throw new RuntimeException("외부 서버 통신 오류가 발생했습니다.");
            default:
                throw new RuntimeException("알 수 없는 오류 발생. 상태코드: " + statusCode);
        }

        // 3. 유효한 경우: companyName은 optional (DB 조회 실패해도 문제 없음)
        String companyName = null;
        CheckCompanyResponseDto company = receiptProcessMapper.findCompanyInfoByCompanyId(dto.getCompanyId());
        if (company != null) {
            companyName = company.getCompanyName();
        } else {
            throw new NoSuchElementException("");
        }

        // 4. valid 성공 응답
        return CheckCompanyResponseDto.builder()
                .isValid(true)
                .companyId(dto.getCompanyId())
                .companyName(companyName)
                .ceoName(dto.getCeoName())
                .openedDate(dto.getOpenedDate())
                .build();

    }

    // 사업장 선택 조회
    public List<ReceiptSelectDto> getCompanySelectionListByUserId(Long userId) {
        List<ReceiptSelectDto> companyList =  receiptProcessMapper.findCompanySelectionListByUserId(userId);
        if(companyList.isEmpty()){
            throw new NoSuchElementException("");
        }
        return companyList;
    }

    // 영수 처리 정보 조회
    public ReceiptProcessCheckDto getCompanyInfoByReceiptId(Long receiptId) {
        return receiptProcessMapper.findCompanyInfoByReceiptId(receiptId);
    }


    // 영수 처리 요청
    public void upsertReceiptProcess(ReceiptProcessRequestDto dto, Long userId) {

        if (dto.getCompanyId() == null) {
            throw new IllegalArgumentException("companyId는 필수입니다.");
        }

        // 1. receiptId 유효성 검사
        if (dto.getReceiptId() == null) {
            throw new IllegalArgumentException("receiptId는 필수입니다.");
        }

        // 2. 해당 영수증이 userId 소유인지 확인 (ceo 권한 검증)
        Long ceoId = receiptProcessMapper.ceoIdByCompanyId(dto.getCompanyId());
        if (ceoId == null) {
            throw new IllegalArgumentException("해당 기업에 대한 대표 정보가 올바르지 않거나, 없습니다.");
        }
        dto.setCeoId(ceoId);
        // 3. receipt_process 존재 여부 확인
        boolean exists = receiptProcessMapper.existsReceiptProcessByReceiptId(dto.getReceiptId());

        // 4. 분기 처리
        if (exists) {
            // update: progressType, detail, voucher 등 변경
            receiptProcessMapper.updateReceiptProcess(dto);
        } else {
            // insert: 최초 등록 (process_state = 'inProgress')
            receiptProcessMapper.insertReceiptProcess(
                    dto.getCeoId(),
                    dto.getProgressType(),
                    dto.getProgressDetail(),
                    dto.getFileName(),
                    dto.getReceiptId()
            );
        }
    }
}