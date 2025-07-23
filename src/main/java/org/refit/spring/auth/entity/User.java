package org.refit.spring.auth.entity;

import lombok.*;
import org.refit.spring.auth.enums.UserRole;

import java.time.LocalDate;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId;
    private String username;
    private String password;
    private String name;
    private String birthDate;
    private Long totalCarbonPoint;
    private Long totalStarPoint;
    //enum으로 사업자/일반유저 분리
    private UserRole role;
    private String refreshToken;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
