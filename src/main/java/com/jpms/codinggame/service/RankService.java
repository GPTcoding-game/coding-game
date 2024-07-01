package com.jpms.codinggame.service;

import com.jpms.codinggame.dto.RankResDto;
import com.jpms.codinggame.entity.Tier;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankService {
    private final UserRepository userRepository;
    private final RedisService redisService;

    /*
    * 당일 랭킹
    * */
    public List<RankResDto> getTodayRankList(){
        List<RankResDto> rankList = new ArrayList<>();

        //key : userId
        Set<String> keys = redisService.getAllKeys();

        for (String key : keys) {
            Object scoreObj = redisService.get(key,"score");
            Object nicknameObj = redisService.get(key,"nickname");
            RankResDto rankResDto = new RankResDto();

            rankResDto.setNickname((String) nicknameObj);
            rankResDto.setScore((int) scoreObj);

            rankList.add(rankResDto);
        }

        rankList.sort(new Comparator<RankResDto>() {
            @Override
            public int compare(RankResDto o1, RankResDto o2) {
                return Integer.compare(o1.getScore(),o2.getScore());
            }
        });

        return rankList;
    }

    /*
    * 누적 랭킹 50명
    * */
    public List<RankResDto> getAllDayRankList(){
        //50명만 뽑기
        List<User> userList = userRepository.findTop50ByOrderByTotalScoreDesc();
        return userList
                .stream()
                .map(user -> RankResDto
                        .builder()
                        .nickname(user.getNickName())
                        .score(user.getTotalScore())
                        .build())
                .toList();
    }

    /*
    * 지역 랭킹
    * */

    /*
    * 나의 오늘 랭킹 가져오기
    * */
    public int getMyTodayRank(User user){
        List<RankResDto> rankList = getTodayRankList();
        int todayRank = 0;

        for (RankResDto dto : rankList) {
            if(dto.getNickname().equals(user.getUserName())){
                todayRank = rankList.indexOf(dto) + 1;
                break;
            }
        }

        return todayRank;
    }

    /*
    * 나의 누적 랭킹 가져오기
    * */
    public int getMyAllDayRank(User user){
        return userRepository.findRankByTotalScore(user.getTotalScore());
    }

    /*
    * 티어 변경 메서드
    * */
    public Tier getUserTier(User user){
        if(bronze_range(user.getTotalScore())){
            return Tier.BRONZE;
        }
        else if(silver_range(user.getTotalScore())){
            return Tier.SILVER;
        }
        else if(gold_range(user.getTotalScore())){
            return Tier.GOLD;
        }
        else if(platinum_range(user.getTotalScore())){
            return Tier.PLATINUM;
        }
        else{
            return Tier.DIAMOND;
        }
    }

    public boolean bronze_range(int totalScore){
        return totalScore <= 100 && totalScore >= 0;
    }
    public boolean silver_range(int totalScore){
        return totalScore <= 200 && totalScore > 100;
    }
    public boolean gold_range(int totalScore){
        return totalScore <= 300 && totalScore > 200;
    }
    public boolean platinum_range(int totalScore){
        return totalScore <= 400 && totalScore > 300;
    }
    public boolean diamond_range(int totalScore){
        return totalScore > 400;
    }
}
