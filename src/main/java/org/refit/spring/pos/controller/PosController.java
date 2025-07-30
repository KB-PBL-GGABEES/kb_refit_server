package org.refit.spring.pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Response;
import lombok.RequiredArgsConstructor;
import org.refit.spring.pos.dto.PosResponseDto;
import org.refit.spring.pos.service.PosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "POS", description = "POS관련 API입니다.")
@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class PosController {
    private final PosService posService;

    @ApiOperation(value = "POS 상품 조회", notes = "POS 기계에서 영수증을 찍을 수 있도록 현재 상점의 상품 목록을 조회할 수 있습니다.")
    @GetMapping("/merchandise/{companyId}")
    public ResponseEntity<?> getMerchandise(@PathVariable("companyId") Long companyId) {
        PosResponseDto.GetMerchandiseListDto list = posService.getMerchandiseList(companyId);
        if (list == null) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "모든 company 조회", notes = "POS 기계에서 모든 가게 리스트를 조회할 수 있습니다.")
    @GetMapping("/company")
    public ResponseEntity<?> getCompanyList() {
        PosResponseDto.GetCompanyListDto list = posService.getCompanyList();
        if (list == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

}
