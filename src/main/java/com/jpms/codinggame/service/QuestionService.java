package com.jpms.codinggame.service;

import com.jpms.codinggame.Oauth2.PrincipalDetails;
import com.jpms.codinggame.dto.QuestionResDto;
import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.entity.QuestionType;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.repository.QuestionRepository;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public List<QuestionResDto> getQuestionList(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        int todayScore = (int) redisService.getTodayScore(user);
        List<Question> questionList = questionRepository.findAllByDate(LocalDate.now());

        //만약 모든 문제를 맞혔다면 exception
        if(todayScore == questionList.size()){
            throw new CustomException(ErrorCode.TODAY_QUESTION_ALL_SOLVED);
        }

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
                .toList()
                .subList(todayScore,questionList.size()-1); //맞은 문제 빼고 다음문제부터 준다.
    }

//    /*
//    * 틀린 문제 가져오기
//    * */
//    public List<QuestionResDto> getIncorrectQuestionList(Authentication authentication){
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        User user = principalDetails.getUser();
//
//        List<Question> questionList = userRepository
//                .findById(user.getId())
//                .orElseThrow(RuntimeException::new)
//                .getQuestionList();
//
//        return questionList
//                .stream()
//                .map(question -> QuestionResDto
//                        .builder()
//                        .questionId(question.getId())
//                        .questionNo(question.getQuestionNo())
//                        .content(question.getContent())
//                        .choice(question.getChoice())
//                        .answer(question.getAnswer())
//                        .build())
//                .toList();
//    }
//
//    /*
//    * 유형별 문제 가져오기
//    * */
//    public List<QuestionResDto> getQuestionListByType(String questionType){
//        List<Question> questionList = questionRepository
//                .findAllQuestionByDateAndType(questionType,LocalDate.now());
//        return questionList
//                .stream()
//                .map(question -> QuestionResDto
//                        .builder()
//                        .questionId(question.getId())
//                        .questionNo(question.getQuestionNo())
//                        .content(question.getContent())
//                        .choice(question.getAnswer())
//                        .answer(question.getAnswer())
//                        .build())
//                .toList();
//    }
//
//    /*
//    * 이전 문제 전부 가져오기
//    * (REFACTOR : 날짜별 문제 가져오기)
//    * */
//    public List<QuestionResDto> getPastQuestionByDate(LocalDate date){
//        List<Question> questionList = questionRepository.findAllByDate(date);
//        return questionList
//                .stream()
//                .map(question -> QuestionResDto
//                        .builder()
//                        .questionId(question.getId())
//                        .questionNo(question.getQuestionNo())
//                        .content(question.getContent())
//                        .choice(question.getChoice())
//                        .answer(question.getAnswer())
//                        .build())
//                .toList();
//    }

    /*
    * 지난 문제 검색 메서드
    * */

    public List<QuestionResDto> getQuestionListByCondition(
            Authentication authentication,
            String date,
            String questionType,
            boolean incorrect,
            Long cursor
    ){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        //일단 틀린문제를 체크했을 경우를 따짐 ( 틀린문제는 user 에 있기 때문 )
        if(incorrect){
            //틀린 문제 List 를 가져옴
            List<Question> incorrectQuestionList = user.getQuestionList();

            //questionList 가 빈값이면 틀린 문제 없음 exception
            if(incorrectQuestionList.isEmpty()){
                throw new CustomException(ErrorCode.INCORRECT_QUESTION_NOT_FOUND);
            }

            //Response 에 들어온 cursor 의 값이 index 값과 같거나 넘어갈때 exception
            if(isCursorEndOfIndex(incorrectQuestionList,cursor)){
                throw new CustomException(ErrorCode.OUT_OF_QUESTION_INDEX);
            }

            if(questionType == null && date != null){
                LocalDate targetDate = LocalDate.parse(date);

                List<Question> filteredQuestionList = incorrectQuestionList
                        .stream()
                        .filter(question -> question.getDate().equals(targetDate))
                        .toList();
                return getIncorrectQuestionResDtoList(cursor, filteredQuestionList);
            }
            else if(questionType != null && date == null){

                List<Question> filteredQuestionList = incorrectQuestionList
                        .stream()
                        .filter(question -> question.getQuestionType().equals(questionType))
                        .toList();
                return getIncorrectQuestionResDtoList(cursor, filteredQuestionList);
            }
            else if(date != null && questionType != null){
                LocalDate targetDate = LocalDate.parse(date);

                List<Question> filteredQuestionList = incorrectQuestionList
                        .stream()
                        .filter(question ->
                                        question.getDate().equals(targetDate)
                                        &&
                                        question.getQuestionType().equals(questionType))
                        .toList();
                return getIncorrectQuestionResDtoList(cursor, filteredQuestionList);
            }
            else{
                return getIncorrectQuestionResDtoList(cursor, incorrectQuestionList);
            }
        }
        //incorrect == false
        else{
            if(questionType == null && date != null){
                List<Question> questionList = questionRepository
                        .findAllByDateByCursor(LocalDate.parse(date),cursor,createNextCursor(cursor));
                return getQuestionResDtoList(cursor, questionList);
            }
            else if(questionType != null && date == null){
                List<Question> questionList = questionRepository
                        .findAllByQTypeByCursor(QuestionType.valueOf(questionType),cursor,createNextCursor(cursor));
                return getQuestionResDtoList(cursor, questionList);
            }
            else if(questionType != null && date != null){
                List<Question> questionList = questionRepository
                        .findAllByDateAndQTypeByCursor(LocalDate.parse(date),questionType,cursor,createNextCursor(cursor));
                return getQuestionResDtoList(cursor, questionList);
            }
            else{
                List<Question> questionList = questionRepository.findAllByCursor(cursor, createNextCursor(cursor));
                return getQuestionResDtoList(cursor, questionList);
            }
        }
    }

    /*
    * 분리된 메서드
    * */

    private List<QuestionResDto> getIncorrectQuestionResDtoList(Long cursor, List<Question> incorrectQuestionList) {
        if(isNextCursorEndOfIndex(incorrectQuestionList, cursor)){
            throw new CustomException(ErrorCode.OUT_OF_QUESTION_INDEX);
        }
        return fromEntityList(incorrectQuestionList).subList(cursor.intValue(), createNextCursor(cursor).intValue());
    }

    private List<QuestionResDto> getQuestionResDtoList(Long cursor, List<Question> questionList) {
        if(isCursorEndOfIndex(questionList, cursor)){
            throw new CustomException(ErrorCode.OUT_OF_QUESTION_INDEX);
        }
        return fromEntityList(questionList);
    }

    public List<QuestionResDto> fromEntityList(List<Question> questionList){
        return questionList
                .stream()
                .map(question -> QuestionResDto
                        .builder()
                        .questionId(question.getId())
                        .content(question.getContent())
                        .choice(question.getChoice())
                        .answer(question.getAnswer())
                        .build())
                .toList();
    }

    /*
    * subList 뽑아낼 때, next-cursor 가 인덱스보다 크다면 size() 까지만
    * */
    public boolean isNextCursorEndOfIndex(List<Question> questionList , Long cursor){
        long nextCursor = cursor + 5;
        int listSize = questionList.size();
        return nextCursor > listSize;
    }

    public boolean isCursorEndOfIndex(List<Question> questionList, Long cursor){
        int listSize = questionList.size();
        return cursor >= listSize;
    }

    public Long createNextCursor(Long cursor){
        return cursor+5;
    }


}
