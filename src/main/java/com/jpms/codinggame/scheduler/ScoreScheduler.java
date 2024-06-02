package com.jpms.codinggame.scheduler;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.repository.UserRepository;
import com.jpms.codinggame.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScoreScheduler {
    private final RedisService redisService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateScore(){
        Set<String> userIdSet = redisService.getAllKeys();

        for (String key : userIdSet) {
            //당일 점수
            int todayScore = (int) redisService.get(key,"score");

            Optional<User> optionalUser = userRepository.findById(Long.parseLong(key));
            if(optionalUser.isEmpty()) continue;

            //DB 에 당일 점수 update
            User user = optionalUser.get();
            user.updateScore(user.getScore() + todayScore);

            //당일 점수 REDIS 에서 삭제
            redisService.delete(key,"score");
        }


    }

    @Scheduled(cron = "1 0 0 * * ?")
//    @Scheduled(fixedDelay = 30000)
    public void updateState(){
        Set<String> userIdSet = redisService.getAllKeys();

        for (String key : userIdSet) {

            //isDone = true 로 설정
            userRepository.updateState(Long.parseLong(key));

            //redis 값 삭제
            redisService.delete(key);
        }
    }

}
