package com.jpms.codinggame.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MainInfoResDto {

    private String nickName;
    private String tier;
    private String todayRank;
    private String allDayRank;
    private String todayScore;
    private String possibleCount;
    private String pictureUrl;

}
