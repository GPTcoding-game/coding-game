package com.jpms.codinggame.global.dto;


import com.jpms.codinggame.entity.Tier;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    private String userName;

    private String nickName;

    private int totalScore;

    private Tier tier;

    private String address;

    private int todayRank;

    private String picture;
}
