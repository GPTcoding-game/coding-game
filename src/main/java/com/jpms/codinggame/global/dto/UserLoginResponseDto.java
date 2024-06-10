package com.jpms.codinggame.global.dto;

import com.jpms.codinggame.entity.Role;
import com.jpms.codinggame.entity.Tier;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginResponseDto {
    private Long id;
    private String userName;
    private String email;
    private Tier tier;
    private int score;
    private boolean isDone;
    private Role role;
    private String address;
    private String provider;
    private String providerId;
    private String accessToken;
    private String refreshToken;
}
