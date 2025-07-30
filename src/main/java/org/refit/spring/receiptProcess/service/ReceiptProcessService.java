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

    private final ReceiptProcessMapper receiptProcessMapper;
    private final RestTemplate restTemplate;

    @Value("${openapi.api-key}")
    private String apiKey;

    @Value("${openapi.business-validate-url}")
    private String validateUrl;

    @SuppressWarnings("unchecked")
    public boolean verifyCompany(CheckCompanyRequestDto requestDto) {
        String url = UriComponentsBuilder.fromHttpUrl(validateUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("returnType", "JSON")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OpenApiValidateRequestDto> entity =
                new HttpEntity<>(OpenApiValidateRequestDto.from(requestDto), headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.getBody().get("data");
                if (dataList != null && !dataList.isEmpty()) {
                    String valid = (String) dataList.get(0).get("valid");
                    return "01".equals(valid);
                }
            }
//            에러 처리 로직
        } catch (Exception e) {
            log.error("사업자 진위 확인 중 예외 발생", e);
        }

        return false;
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