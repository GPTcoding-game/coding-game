package com.jpms.codinggame.repository.question;

import com.jpms.codinggame.entity.Question;

import java.time.LocalDate;
import java.util.List;

public interface QuestionRepositoryCustom {

    //QuestionNo 지정 메서드
    int findMaxQuestionNoByDate(LocalDate date);

    //Date 별 문제 호출
    List<Question> findAllByDate(LocalDate date);

    //Date & QuestionType 파라미터 받았을 때 적용되는 쿼리
    List<Question> findAllByDateAndQTypeByCursor(LocalDate date, String questionType, Long cursor, Long nextCursor);

    //Date 파라미터로 받았을 때 적용되는 쿼리
    List<Question> findAllByDateByCursor(LocalDate date, Long cursor, Long nextCursor);

    //QuestionType 파라미터로 받았을 때 적용되는 쿼리
    List<Question> findAllByQTypeByCursor(String questionType, Long cursor, Long nextCursor);

    //전체 검색 시 적용되는 쿼리
    List<Question> findAllByCursor(Long cursor, Long nextCursor);
}
