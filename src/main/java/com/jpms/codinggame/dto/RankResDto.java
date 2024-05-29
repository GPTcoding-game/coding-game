package com.jpms.codinggame.dto;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankResDto {
    private String userName;
    private int score;
}
