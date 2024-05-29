package com.jpms.codinggame.service;

import com.jpms.codinggame.dto.RankResDto;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
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
    public List<RankResDto> getTodayRank(){
        List<RankResDto> rankList = new ArrayList<>();

        //key : userId
        Set<String> keys = redisService.getAllKeys();

        for (String key : keys) {
            Object scoreObj = redisService.get(key,"score");
            Object usernameObj = redisService.get(key,"username");
            RankResDto rankResDto = new RankResDto();

            rankResDto.setUserName((String) usernameObj);
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
    * 누적 랭킹
    * */
    public List<RankResDto> getAllDayRank(){
        //todo : 누적 랭킹 로직 작성
        //50명만 뽑기
        List<User> userList = userRepository.findTop50ByOrderByScoreDesc();
        return userList
                .stream()
                .map(user -> RankResDto
                        .builder()
                        .userName(user.getUserName())
                        .score(user.getScore())
                        .build())
                .toList();
    }

    /*
    * 지역 랭킹
    * */
}
