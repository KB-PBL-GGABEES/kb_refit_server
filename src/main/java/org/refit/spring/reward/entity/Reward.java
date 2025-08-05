package org.refit.spring.reward.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reward {
    private Long rewardId;
    private Long userId;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Long carbonPoint;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Long reward;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;
}
