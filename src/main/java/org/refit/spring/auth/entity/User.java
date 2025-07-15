package org.refit.spring.auth.entity;

import lombok.*;
import org.refit.spring.auth.enums.UserRole;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    //enum으로 사업자/일반유저 분리
    private UserRole role;
    private String refreshToken;
}
