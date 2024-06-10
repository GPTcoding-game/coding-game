package com.jpms.codinggame.service;

import com.jpms.codinggame.dto.CheckAnswerReqDto;
import com.jpms.codinggame.dto.CheckAnswerReqDto2;
import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.repository.QuestionRepository;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final RedisService redisHashService;


    /*
    * 처음 참여하는지 확인
    * */
    public boolean isFirst(Authentication authentication){
        return redisHashService.isEmptyKey(String.valueOf(authentication.getPrincipal()));
    }

    /*
    * 참여제한 여부 확인
    * */
    public boolean isDone(Authentication authentication){
        User user = userRepository
                .findById((Long)authentication.getPrincipal())
                .orElseThrow(RuntimeException::new);

        if(!user.isDone()) return false;

        //참여횟수 0이면 isDone 필드값 false 로 상태변경
        if((int) redisHashService.get(String.valueOf(user.getId()),"possibleCount") < 0) {
            user.updateIsDone(false);
            userRepository.save(user);
        };

        return user.isDone();
    }

    /*
    * 참여가능 횟수 차감 메서드
    * */
    public void deductPossibleCount(Authentication authentication){
        User user = userRepository.findById((Long)authentication.getPrincipal()).orElseThrow();
        //참여횟수 카운트 -1
        redisHashService.put(String.valueOf(user.getId()),"possibleCount",(int)redisHashService.getPossibleCount(user) - 1);
        //로그
        log.info(String.valueOf(redisHashService.getPossibleCount(user)));
    }

    /*
    * 최초 게임시작 시 redis data 세팅 메서드
    * */
    public void setRedisData(Authentication authentication){
        //유저 데이터
        User user = userRepository
                .findById((Long)authentication.getPrincipal())
                .orElseThrow(RuntimeException::new);

        //REDIS 데이터 생성

        //Key : userId , HashKey : "username" , Value : username
        redisHashService.put(String.valueOf(user.getId()),"nickname",user.getNickName());
        //Key : userId , HashKey : "score" , Value : score
        redisHashService.put(String.valueOf(user.getId()),"score",0);
        //Key : userId, HashKey : "possibleCount", Value : count (최초 3)
        redisHashService.put(String.valueOf(user.getId()),"possibleCount",3);
    }

    /*
    * 재도전 메서드
    * */

    /*
    * 게임시작 메서드
    * */

    /*
    * 채점로직
    * */
//    public void checkAnswer(Authentication authentication, CheckAnswerReqDto dto){
//        User user = userRepository
//                .findById((Long)authentication.getPrincipal())
//                .orElseThrow(RuntimeException::new);
//        List<Integer> incorrectQuestionNumberList = dto.getIncorrectNumber();
//
//        //틀린 문제 리스트를 user 에 저장 (추후 틀린문제 다시보기에 활용)
//        List<Question> incorrectQuestionList = user.getQuestionList();
//
//        //QuestionNo 와 Date 로 틀린 문제 idx 특정.
//        for (int i = 0; i < incorrectQuestionNumberList.size(); i++) {
//            Question question =
//                    questionRepository.findAllByDateAndQuestionNo(LocalDate.now(),incorrectQuestionNumberList.get(i));
//
//            //만약 이전에 틀린 적이 있다면 넘어감
//            if(incorrectQuestionList.contains(question)) continue;
//
//            incorrectQuestionList
//                    .add(question);
//        }
//
//        //틀린문제, 참여가능여부 업데이트
//        user.updateQuestionList(incorrectQuestionList);
//        user.updateIsDone(false);
//
//        //Key : userId , HashKey : "username" , Value : username
//        redisHashService.put(String.valueOf(user.getId()),"nickname",user.getNickName());
//        //Key : userId , HashKey : "score" , Value : score
//        redisHashService.put(String.valueOf(user.getId()),"score",dto.getScore());
//    }


    /*
    *  오늘의 게임 채점 로직 ( 맞으면 redis 에 점수 업데이트 , 틀리면 틀린문제 리스트에 저장 )
    * */
    public void checkAnswerTodayQuestion(Authentication authentication, CheckAnswerReqDto2 dto){
        User user = userRepository
                .findById((Long)authentication.getPrincipal())
                .orElseThrow(RuntimeException::new);

        Question question = questionRepository
                .findById(dto.getQuestionId())
                .orElseThrow(RuntimeException::new);

        List<Question> questionList = user.getQuestionList();


        //정답일 경우 (isCorrect == true)
        if(dto.isCorrect()){
            redisHashService.put(String.valueOf(user.getId()),"score", (int)redisHashService.getTodayScore(user)+1);
//            log.info("Today score : {}", String.valueOf(redisHashService.getTodayScore(user)));
        }
        //오답일 경우 (isCorrect == false)
        else{
            if(!questionList.contains(question)){
                questionList.add(question);
                user.updateQuestionList(questionList);
                userRepository.save(user);
            }
        }
    }

    /*
    * 지난 문제 다시풀기 채점로직 ( 틀린것만 업데이트, 맞으면 그냥 넘어감 )
    * */
    public void checkAnswerPastQuestion(Authentication authentication, CheckAnswerReqDto2 dto){
        User user = userRepository
                .findById((Long)authentication.getPrincipal())
                .orElseThrow(RuntimeException::new);

        Question question = questionRepository
                .findById(dto.getQuestionId())
                .orElseThrow(RuntimeException::new);

        List<Question> questionList = user.getQuestionList();

        //오답일 경우
        if(!dto.isCorrect()){
            if(!questionList.contains(question)){
                questionList.add(question);
                user.updateQuestionList(questionList);
                userRepository.save(user);
            }
        }
    }

    /*
    * 게임 중도포기
    * */
    public void stopGame(Authentication authentication){
        User user = userRepository
                .findById((Long)authentication.getPrincipal())
                .orElseThrow(RuntimeException::new);
//        user.updateIsDone(false);
        redisHashService.put(String.valueOf(user.getId()),"possibleCount",(int)redisHashService.getPossibleCount(user)-1);
    }
}
