package com.jpms.codinggame.repository;

import com.jpms.codinggame.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT COALESCE(MAX(q.questionNo), 0) FROM Question q WHERE q.date = :date")
    int findMaxQuestionNoByDate(@Param("date") LocalDate date);

    //날짜 별 문제 조회
    @Query("SELECT q FROM Question q WHERE q.date = :date")
    List<Question> findAllByDate(@Param("date") LocalDate date);

    //문제 넘버 와 날짜 별 문제 조회
    @Query("SELECT q FROM Question q WHERE q.date = :date AND q.questionNo = :questionNo")
    Question findAllByDateAndQuestionNo(
            @Param("date") LocalDate date,
            @Param("questionNo") int questionNo);

    List<Question> findAllByQuestionType(String questionType);

    @Query("SELECT q FROM Question q WHERE q.questionType = :questionType AND q.date != :date ")
    List<Question> findAllQuestionByDateAndType(
            @Param("questionType") String questionType,
            @Param("date") LocalDate date);
}
