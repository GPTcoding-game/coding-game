package com.jpms.codinggame.global.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetInfoResponseDto {
    private String email;
    private String nickName;
    private String address;
}
