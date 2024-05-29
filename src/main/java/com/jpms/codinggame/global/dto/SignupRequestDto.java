package com.jpms.codinggame.global.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    private String username;

    private String password;

    private String checkPassword;

    private String address;

    private String email;

}
