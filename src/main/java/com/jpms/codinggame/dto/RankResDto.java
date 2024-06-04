package com.jpms.codinggame.dto;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankResDto {
    private String nickname;
    private int score;
}
