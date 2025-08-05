package org.refit.spring.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.refit.spring.auth.entity.User;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String role;
    private String name;
    private String birthDate;
    private Long totalCarbonPoint;
    private Long totalStarPoint;

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getRole().name(),
                user.getName(),
                user.getBirthDate(),
                user.getTotalCarbonPoint(),
                user.getTotalStarPoint()
        );
    }
}
