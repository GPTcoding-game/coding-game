package com.jpms.codinggame.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionDataDto {
    private String email;
    private String username;
    private String provider;
}
