package org.refit.spring.pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.models.Response;
import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.annotation.UserId;
import org.refit.spring.auth.service.UserService;
import org.refit.spring.pos.dto.PosResponseDto;
import org.refit.spring.pos.service.PosService;
import org.refit.spring.receipt.dto.ReceiptContentRequestsDto;
import org.refit.spring.receipt.dto.ReceiptRequestDto;
import org.refit.spring.receipt.dto.ReceiptResponseDto;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.service.ReceiptService;
import org.refit.spring.reward.entity.Reward;
import org.refit.spring.reward.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Api(tags = "POS", description = "POS관련 API입니다.")
@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class PosController {
    private final PosService posService;
    private final RewardService rewardService;

    private final UserService userService;
    private final ReceiptService receiptService;

    private static final long CARBON_POINT = 100L;

    @ApiOperation(value = "POS 상품 조회", notes = "POS 기계에서 영수증을 찍을 수 있도록 현재 상점의 상품 목록을 조회할 수 있습니다.")
    @GetMapping("/merchandise/{companyId}")
    public ResponseEntity<?> getMerchandise(@PathVariable("companyId") Long companyId) {
        PosResponseDto.GetMerchandiseListDto list = posService.getMerchandiseList(companyId);
        if (list == null) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(list);
    }

    //내 사업장만 뜨도록 추가
    @ApiOperation(value = "로그인한 사용자의 모든 company 조회", notes = "POS 기계에서 로그인한 사업자의 모든 가게 리스트를 조회할 수 있습니다.")
    @GetMapping("/company")
    public ResponseEntity<?> getCompanyList(@ApiIgnore @UserId Long userId) {
        PosResponseDto.GetCompanyListDto list = posService.getMyCompanyList(userId);
        if (list == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "영수증 등록", notes = "결제 시 영수증이 생성됩니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @PostMapping("/create")
    public ResponseEntity<?> create(@ApiIgnore @UserId Long userId,
                                    @RequestBody ReceiptRequestDto receiptRequestDto) throws SQLException {
        try {
            Map<String, Object> requiredFields = new HashMap<>();
            requiredFields.put("cardId", receiptRequestDto.getCardId());
            requiredFields.put("contentsList", receiptRequestDto.getContentsList());
            for (ReceiptContentRequestsDto dto: receiptRequestDto.getContentsList()) {
                requiredFields.put("amount", dto.getAmount());
                requiredFields.put("merchandiseId", dto.getMerchandiseId());
            }
            receiptService.validateRequiredFields(requiredFields);
            Receipt receipt = receiptService.create(receiptRequestDto);
            Reward reward = rewardService.create(CARBON_POINT, receipt.getTotalPrice(), receipt.getUserId(), receipt.getReceiptId());
            userService.updatePoint(userId, reward.getCarbonPoint(), reward.getReward());
            ReceiptResponseDto dto = ReceiptResponseDto.from(receipt, reward.getCarbonPoint(), reward.getReward(), "none");
            receiptService.checkAndInsertBadge(userId, receipt.getReceiptId());
            URI location = URI.create("/receipt/" + receipt.getReceiptId());

            return ResponseEntity.created(location).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 내부 오류가 발생했습니다.");
        }

    }

    @ApiOperation(value = "환불 영수증 등록", notes = "결제 시 생성된 영수증 아이디를 이용해 환불 영수증을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @PostMapping("/refund")
    public ResponseEntity<?> refund(@ApiIgnore @UserId Long userId,
                                    @RequestParam(required = false) Long receiptId) {
        try {
            Map<String, Object> requiredFields = new HashMap<>();
            requiredFields.put("receiptId", receiptId);
            receiptService.validateRequiredFields(requiredFields);
            Receipt receipt = receiptService.refund(userId, receiptId);
            Reward reward = rewardService.create(-CARBON_POINT, receipt.getTotalPrice(), userId, receiptId);
            userService.updatePoint(userId, reward.getCarbonPoint(), reward.getReward());
            ReceiptResponseDto dto = ReceiptResponseDto.from(receipt, reward.getCarbonPoint(), reward.getReward(), "none");
            URI location = URI.create("/receipt/" + receipt.getReceiptId());
            return ResponseEntity.created(location).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 내부 오류가 발생했습니다.");
        }

    }

}
