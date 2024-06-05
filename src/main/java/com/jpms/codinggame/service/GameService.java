package com.jpms.codinggame.service;

import com.jpms.codinggame.dto.CheckAnswerReqDto;
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
    * 참여제한 여부 확인
    * */
    public boolean isDone(Authentication authentication){
        User user = userRepository
                .findById((Long)authentication.getPrincipal())
                .orElseThrow(RuntimeException::new);
        return user.isDone();
    }

    /*
    * 채점로직
    * */
    public void checkAnswer(Authentication authentication, CheckAnswerReqDto dto){
        User user = userRepository
                .findById((Long)authentication.getPrincipal())
                .orElseThrow(RuntimeException::new);
        List<Integer> incorrectQuestionNumberList = dto.getIncorrectNumber();

        //틀린 문제 리스트를 user 에 저장 (추후 틀린문제 다시보기에 활용)
        List<Question> incorrectQuestionList = user.getQuestionList();

        //QuestionNo 와 Date 로 틀린 문제 idx 특정.
        for (int i = 0; i < incorrectQuestionNumberList.size(); i++) {
            Question question =
                    questionRepository.findAllByDateAndQuestionNo(LocalDate.now(),incorrectQuestionNumberList.get(i));

            //만약 이전에 틀린 적이 있다면 넘어감
            if(incorrectQuestionList.contains(question)) continue;

            incorrectQuestionList
                    .add(question);
        }

        //틀린문제, 참여가능여부 업데이트
        user.updateQuestionList(incorrectQuestionList);
        user.updateIsDone(false);

        //Key : userId , HashKey : "username" , Value : username
        redisHashService.put(String.valueOf(user.getId()),"nickname",user.getNickName());
        //Key : userId , HashKey : "score" , Value : score
        redisHashService.put(String.valueOf(user.getId()),"score",dto.getScore());
    }

    /*
    * 게임 중도포기
    * */
    public void stopGame(Authentication authentication){
        User user = userRepository
                .findById((Long)authentication.getPrincipal())
                .orElseThrow(RuntimeException::new);
        user.updateIsDone(false);
        redisHashService.put(String.valueOf(user.getId()),"score",0);
    }
}
