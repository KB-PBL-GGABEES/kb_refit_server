package org.refit.spring.ceo.controller;

import lombok.RequiredArgsConstructor;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;
import org.refit.spring.ceo.service.CeoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ceo")
public class CeoController {
    final CeoService ceoService;

    // 경비 처리가 필요한 내역 조회
    @GetMapping("/undone")
    public ResponseEntity<List<CeoListDto>> getListUndone() {
        List<CeoListDto> list = ceoService.getListUndone();
        return ResponseEntity.ok(list);
    }

    // 경비 청구 항목 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<ReceiptDetailDto> getReceiptDetail(
            @RequestParam("id") Long receipted) {
        return ResponseEntity.ok(ceoService.getReceiptDetail(receipted));
    }

    // 경비 처리 완료 내역 조회
    @GetMapping("/done")
    public ResponseEntity<List<CeoListDto>> getListDone(
            @RequestParam(value = "period", defaultValue = "1")
            int period) {
        List<CeoListDto> list = ceoService.getListDone(period);
        return ResponseEntity.ok(list);
    }

    // 처리 완료된 항목 이메일 전송

    // 영수 처리 승인

    // 영수 처리 반려

    // 한달 법카 금액 조회

    // 법카 내역 조회

    // 페이지네이션

}
