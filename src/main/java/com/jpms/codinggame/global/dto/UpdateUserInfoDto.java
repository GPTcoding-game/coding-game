package com.jpms.codinggame.global.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoDto {

    private String password;

    private String checkPassword;

    private String nickName;

    private String address;

}

