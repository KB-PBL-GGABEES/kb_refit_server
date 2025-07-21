package org.refit.spring.hospital.controller;

import lombok.RequiredArgsConstructor;
import org.refit.spring.hospital.dto.HospitalExpenseDetailResponseDto;
import org.refit.spring.hospital.dto.HospitalExpenseResponseDto;
import org.refit.spring.hospital.dto.InsuranceSubscribedResponseDto;
import org.refit.spring.hospital.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital")
public class HospitalController {
    private final HospitalService hospitalService;

//    @GetMapping("/find")
//    public HospitalExpenseResponseDto findHospitalExpense(@RequestParam("hospitalProcessId") Long hospitalProcessId) {
//        return hospitalService.findHospitalExpenseById(hospitalProcessId);
//    }
  @GetMapping("/")
    public ResponseEntity<?> findHospitalExpense(@RequestParam("hospitalProcessId") Long hospitalProcessId) {
    HospitalExpenseResponseDto result = hospitalService.findHospitalExpenseById(hospitalProcessId);

    if (result == null) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("message", "해당 hospitalProcessId의 데이터가 존재하지 않습니다."));
    }

    return ResponseEntity.ok(result);
}

    @GetMapping("/detail")
    public ResponseEntity<?> findHospitalExpenseDetailById(@RequestParam("receiptId") Long receiptId) {
      HospitalExpenseDetailResponseDto result = hospitalService.findHospitalExpenseDetailById(receiptId);

      if (result == null) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(Collections.singletonMap("message", "해당 receiptId의 데이터가 존재하지 않습니다."));
      }
      return ResponseEntity.ok(result);
    }

    //    @GetMapping("/recent")


    @GetMapping("/insurance")
    public ResponseEntity<?> findInsuranceSubscribeById(@RequestParam("userId") Long userId) {
      List<InsuranceSubscribedResponseDto>
              result = hospitalService.findInsuranceSubscribeById(userId);

      if (result == null || result.isEmpty()) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(Collections.singletonMap("message", "해당 userId의 데이터가 존재하지 않습니다."));
      }
      return ResponseEntity.ok(result);
    }
}