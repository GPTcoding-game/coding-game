package com.jpms.codinggame.scheduler;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.repository.UserRepository;
import com.jpms.codinggame.service.RankService;
import com.jpms.codinggame.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScoreScheduler {
    private final RedisService redisService;
    private final RankService rankService;
    private final UserRepository userRepository;


    /*
    * 레디스에 담겨있는 점수를 DB에 업데이트해줌
    * */
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
            user.updateTier(rankService.getUserTier(user));
            user.updateTotalScore(user.getTotalScore() + todayScore);
            userRepository.save(user);

            //당일 점수 REDIS 에서 삭제
            redisService.delete(key,"score");
        }


    }

    /*
    * 자정에 redis 에 저장된 모든 사용자의 isDone 속성값을 true 로 바꿔줌
    * */

    @Scheduled(cron = "1 0 0 * * ?")
//    @Scheduled(fixedDelay = 30000)
    public void updateState(){
        Set<String> userIdSet = redisService.getAllKeys();

        for (String key : userIdSet) {

            //isDone = true 로 설정
            Optional<User> optionalUser = userRepository.findById(Long.parseLong(key));

            if(optionalUser.isEmpty()) {
                continue;
            }

            User user = optionalUser.get();
            user.updateIsDone(true);
            userRepository.save(user);
            //참여횟수 3으로 설정


            //redis 값 삭제
            redisService.delete(key);
        }
    }

}
