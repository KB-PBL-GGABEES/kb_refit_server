package org.refit.spring.receiptProcess.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.refit.spring.mapper.ReceiptProcessMapper;
import org.refit.spring.receiptProcess.dto.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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


    public CheckCompanyResponseDto verifyAndRegisterEmployee(CheckCompanyRequestDto dto, Long userId) {
        try {
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
            JsonNode statusCode = root.get("status_code");
            if (statusCode.equals("OK")) {
//                성공 로직
            } else {
                if(statusCode.equals("BAD_JSON_REQUEST")) {
//                    오류에 맞는 exception 발생
//                    아니면 사용자한테, 이거에 맞는 Response 반환
                } else if (statusCode.equals("REQUEST_DATA_MALFORMED")) {
//                    필수 파라미터 누락 (이 경우에는 서버 문제일 가능성이 높음)
                }
            }

            // 2. 유효하지 않으면 바로 반환
            if (statusCode.equals("OK")) {
                return CheckCompanyResponseDto.builder()
                        .isValid(false)
                        .companyId(dto.getCompanyId())
                        .ceoName(dto.getCeoName())
                        .openedDate(dto.getOpenedDate())
                        .build();
            }

            // 3. 유효한 경우: companyName은 optional (DB 조회 실패해도 문제 없음)
            String companyName = null;
            try {
                CheckCompanyResponseDto company = receiptProcessMapper.findCompanyInfoByCompanyId(dto.getCompanyId());
                if (company != null) {
                    companyName = company.getCompanyName();
                } else {
//                    TODO: null인 경우에 대한 정보
//                    exception 404 띄워
                }
            } catch (Exception ignored) {
                // 무시
//                여기서 404가 발생하면 사용자한테, -> 우리 서비스에 등록되지 않은 사업자. -> KB 리핏에 가입하지 않은 사업자입니다.
//                return;
            }

            // 4. valid 성공 응답
            return CheckCompanyResponseDto.builder()
                    .isValid(true)
                    .companyId(dto.getCompanyId())
                    .companyName(companyName)
                    .ceoName(dto.getCeoName())
                    .openedDate(dto.getOpenedDate())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("사업자 진위 확인 처리 중 오류", e);
        }
    }

    // 사업장 선택 조회
    public List<ReceiptSelectDto> getCompanySelectionListByUserId(Long userId) {
        return receiptProcessMapper.findCompanySelectionListByUserId(userId);
    }

    // 영수 처리 정보 조회
    public ReceiptProcessCheckDto getCompanyInfoByReceiptId(Long receiptId) {
        return receiptProcessMapper.findCompanyInfoByReceiptId(receiptId);
    }


    // 영수 처리 요청
    public void upsertReceiptProcess(ReceiptProcessRequestDto dto, Long userId) {
        // 1. receiptId 유효성 검사
        if (dto.getReceiptId() == null) {
            throw new IllegalArgumentException("receiptId는 필수입니다.");
        }

        // 2. 해당 영수증이 userId 소유인지 확인 (ceo 권한 검증)
        Long ceoId = receiptProcessMapper.findCeoIdByUserIdAndReceiptId(userId, dto.getReceiptId());
        if (ceoId == null) {
            throw new IllegalArgumentException("해당 영수증은 현재 사용자에게 속하지 않습니다.");
        }

        // 3. receipt_process 존재 여부 확인
        boolean exists = receiptProcessMapper.existsReceiptProcessByReceiptId(dto.getReceiptId());

        // 4. 분기 처리
        if (exists) {
            // update: progressType, detail, voucher 등 변경
            receiptProcessMapper.updateReceiptProcess(dto);
        } else {
            // insert: 최초 등록 (process_state = 'inProgress')
            receiptProcessMapper.insertReceiptProcess(
                    ceoId,
                    dto.getProgressType(),
                    dto.getProgressDetail(),
                    dto.getVoucher(),
                    dto.getReceiptId()
            );
        }
    }

    // receiptId가 실제로 존재하는지 확인

    public boolean receiptExists(Long receiptId) {
        return receiptProcessMapper.existsReceiptProcessByReceiptId(receiptId);
    }

    // userId + receiptId로 ceoId 찾기
    public Long findCeoIdByUserIdAndReceiptId(Long userId, Long receiptId) {
        return receiptProcessMapper.findCeoIdByUserIdAndReceiptId(userId, receiptId);
    }

    // 관련 이미지 파일명 DB조회
    public ReceiptVoucherResponseDto getVoucherFileName(Long receiptId) {
        String fileName = receiptProcessMapper.findVoucherFileNameByReceiptId(receiptId);
        return fileName != null ? new ReceiptVoucherResponseDto(receiptId, fileName) : null;
    }
}