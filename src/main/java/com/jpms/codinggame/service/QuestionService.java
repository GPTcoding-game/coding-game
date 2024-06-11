package com.jpms.codinggame.service;

import com.jpms.codinggame.Oauth2.PrincipalDetails;
import com.jpms.codinggame.dto.QuestionResDto;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final RedisService redisService;
    private final UserRepository userRepository;

    /*
    * 당일 생성 된 문제 가져오기
    * */
    public List<QuestionResDto> getQuestionList(){
        List<Question> questionList = questionRepository.findAllByDate(LocalDate.now());
        return questionList
                .stream()
                .map(question -> QuestionResDto
                        .builder()
                        .questionId(question.getId())
                        .questionNo(question.getQuestionNo())
                        .content(question.getContent())
                        .choice(question.getChoice())
                        .answer(question.getAnswer())
                        .build())
                .toList();
    }

    /*
    * 틀린 문제 가져오기
    * */
    public List<QuestionResDto> getIncorrectQuestionList(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        List<Question> questionList = userRepository
                .findById(user.getId())
                .orElseThrow(RuntimeException::new)
                .getQuestionList();

        return questionList
                .stream()
                .map(question -> QuestionResDto
                        .builder()
                        .questionId(question.getId())
                        .questionNo(question.getQuestionNo())
                        .content(question.getContent())
                        .choice(question.getChoice())
                        .answer(question.getAnswer())
                        .build())
                .toList();
    }

    /*
    * 유형별 문제 가져오기
    * */
    public List<QuestionResDto> getQuestionListByType(String questionType){
        List<Question> questionList = questionRepository
                .findAllQuestionByDateAndType(questionType,LocalDate.now());
        return questionList
                .stream()
                .map(question -> QuestionResDto
                        .builder()
                        .questionId(question.getId())
                        .questionNo(question.getQuestionNo())
                        .content(question.getContent())
                        .choice(question.getAnswer())
                        .answer(question.getAnswer())
                        .build())
                .toList();
    }

    /*
    * 이전 문제 전부 가져오기
    * (REFACTOR : 날짜별 문제 가져오기)
    * */
    public List<QuestionResDto> getPastQuestionByDate(LocalDate date){
        List<Question> questionList = questionRepository.findAllByDate(date);
        return questionList
                .stream()
                .map(question -> QuestionResDto
                        .builder()
                        .questionId(question.getId())
                        .questionNo(question.getQuestionNo())
                        .content(question.getContent())
                        .choice(question.getChoice())
                        .answer(question.getAnswer())
                        .build())
                .toList();
    }
}
