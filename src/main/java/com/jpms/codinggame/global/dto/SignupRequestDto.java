package com.jpms.codinggame.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignupRequestDto {

    private String username;

    private String password;

    private String checkPassword;

    private String address;

    private String email;

}
