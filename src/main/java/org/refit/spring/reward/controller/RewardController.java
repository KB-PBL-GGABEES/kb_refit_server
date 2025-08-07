package org.refit.spring.reward.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.reward.dto.*;
import org.refit.spring.reward.service.RewardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;
import java.util.Collections;
import java.util.Date;

@Api(tags = "리워드 API", description = "리워드 내역과 메인 화면용 포인트 리스트 관련 API입니다.")
@RestController
@RequestMapping("/api/reward")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @ApiOperation(value = "리워드 내역 조회", notes = "탄소중립포인트와 리워드 내역을 목록으로 조회하며, 파라미터를 이용해 필터링할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/list")
    public ResponseEntity<?> getList(
            @ApiIgnore @UserId Long userId,
            @ModelAttribute RewardListRequestDto rewardListRequestDto) {
        try {
            RewardListCursorDto dto = rewardService.getList(userId, rewardListRequestDto);
            return ResponseEntity.ok(dto);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "서버 오류로 인해 실패했습니다."));
        }
    }

    @ApiOperation(value = "포인트 리스트 조회", notes = "메인 페이지 포인트 리스트에서 보여지는 리워드들을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/summary")
    public ResponseEntity<?> getTotalPoint(
            @ApiIgnore @UserId Long userId) {
        RewardSummaryDto dto = rewardService.getTotal(userId);
        return ResponseEntity.ok(dto);
    }

}
