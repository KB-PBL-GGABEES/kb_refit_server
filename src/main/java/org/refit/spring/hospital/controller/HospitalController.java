package org.refit.spring.hospital.controller;

import lombok.RequiredArgsConstructor;
import org.refit.spring.hospital.dto.HospitalExpenseDetailResponseDto;
import org.refit.spring.hospital.dto.HospitalExpenseResponseDto;
import org.refit.spring.hospital.dto.InsuranceSubscribedResponseDto;
import org.refit.spring.hospital.service.HospitalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital")
public class HospitalController {
    private final HospitalService hospitalService;


//    @GetMapping("/")
//    public ResponseEntity<?> getHospitalExpenseList(@RequestParam("userId") Long userId) {
//        List<HospitalExpenseResponseDto> result = hospitalService.findHospitalExpenseById(userId);
//
//        if (result == null || result.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("message", "해당 유저의 병원 영수증 데이터가 없습니다."));
//        }
//
//        return ResponseEntity.ok(result);
//    }


    @GetMapping("/list")
    public ResponseEntity<?> getHospitalExpenses(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "cursorDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date cursorDate) {

        List<HospitalExpenseResponseDto> list = hospitalService.getHospitalExpenses(userId, cursorDate);

        // 조회된 데이터가 없을 경우
        if (list == null || list.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "해당 유저의 병원 영수증 데이터가 없습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error); // 404로 직접 반환
        }

        // 다음 커서 설정
        Date nextCursor = (list.size() < 20) ? null : list.get(list.size() - 1).getCreatedAt();

        // 날짜 포맷 지정
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nextCursorDateStr = (nextCursor != null) ? formatter.format(nextCursor) : null;

        Map<String, Object> response = new HashMap<>();
        response.put("data", list);
        response.put("nextCursorDate", nextCursorDateStr);

        return ResponseEntity.ok(response);
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