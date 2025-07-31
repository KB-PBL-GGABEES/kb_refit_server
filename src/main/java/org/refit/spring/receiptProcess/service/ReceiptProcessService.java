package org.refit.spring.receiptProcess.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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


import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptProcessService {

    // DB 연동용 Mapper
    private final ReceiptProcessMapper receiptProcessMapper;
    // 외부 OpenAPI 호출용 HTTP 클라이언트
    private final RestTemplate restTemplate;

    @Value("${openapi.api-key}")
    // application.properties에서 주입받는 OpenAPI 인증 키
    private String apiKey;

    @Value("${openapi.business-validate-url}")
    // OpenAPI 요청 URL
    private String validateUrl;

    /**
     * 사업자 진위 여부를 확인하는 메서드
     * - OpenAPI에 POST 요청을 보내고, 응답에서 valid 값이 "01"이면 진위 확인 성공
     * - 사업자 번호, 상호명, 주소를 추출하여 CheckCompanyResponseDto로 응답
     * - 실패 또는 예외 상황이면 isValid=false로 응답
     */
    public CheckCompanyResponseDto verifyCompany(CheckCompanyRequestDto requestDto) {
        // 1. OpenAPI URL 구성 (인증키와 응답형식 추가)
        String url = UriComponentsBuilder.fromHttpUrl(validateUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("returnType", "JSON")
                .toUriString();

        // 2. HTTP 헤더 설정 (Content-Type: application/json)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. 요청 바디 생성 (사용자 입력을 OpenAPI 형식으로 변환)
        HttpEntity<OpenApiValidateRequestDto> entity =
                new HttpEntity<>(OpenApiValidateRequestDto.from(requestDto), headers);

        try {
            // 4. OpenAPI에 POST 요청 전송
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    // Map으로 응답 받기
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // 5. 응답 성공 & 바디 존재 확인
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 6. "data" 필드 추출 (사업자 리스트)
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.getBody().get("data");
                if (dataList != null && !dataList.isEmpty()) {
                    // 첫 번째 사업자 정보
                    Map<String, Object> data = dataList.get(0);
                    // 진위 확인 결과
                    String valid = (String) data.get("valid");

                    // 7. valid="01" → 진위 확인 성공
                    if ("01".equals(valid)) {
                        Long companyId = Long.valueOf((String) data.get("b_no")); // 사업자번호
                        String companyName = (String) data.getOrDefault("b_nm", "상호명 없음"); // 상호명
                        String address = (String) data.getOrDefault("addr", "주소 없음"); // 주소

                        // 8. 성공 응답 생성
                        return CheckCompanyResponseDto.builder()
                                .isValid(true)
                                .companyId(companyId)
                                .companyName(companyName)
                                .address(address)
                                .build();
                    }

                    // 9. valid="02" → 진위 확인 실패
                    return CheckCompanyResponseDto.builder()
                            .isValid(false)
                            .build();
                }
            }

        } catch (Exception e) {
            // 10. 예외 발생 시 로그 출력 및 실패 응답 처리
            log.error("사업자 진위 확인 중 예외 발생", e);
        }

        // 11. 정상 응답이 아니거나 예외 발생 시: 진위 확인 실패 처리
        return CheckCompanyResponseDto.builder()
                .isValid(false)
                .build();
    }


    // 사업장 선택 조회
    public List<ReceiptSelectDto> getCompanySelectionListByUserId(Long userId) {
        return receiptProcessMapper.findCompanySelectionListByUserId(userId);
    }

    // 영수 처리 정보 조회
    public ReceiptProcessCheckDto getCompanyInfoByReceiptId(Long receiptId) {
        return receiptProcessMapper.findCompanyInfoByReceiptId(receiptId);
    }

    // 사업자 정보 확인 요청
    public void registerVerifiedCompany(CheckCompanyResponseDto dto) {
        receiptProcessMapper.insertVerifiedCompany(dto);
    }

    // 영수 처리 요청
    public void registerReceiptProcess(ReceiptProcessRequestDto dto, Long ceoId) {
        receiptProcessMapper.insertReceiptProcess(
                ceoId,
                dto.getProgressType(),
                dto.getProgressDetail(),
                dto.getVoucher(),
                dto.getReceiptId()
        );
    }

    // receiptId가 실제로 존재하는지 확인

    public boolean receiptExists(Long receiptId) {
        return receiptProcessMapper.existsReceiptById(receiptId);
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