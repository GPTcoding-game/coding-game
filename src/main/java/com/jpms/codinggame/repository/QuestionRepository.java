package com.jpms.codinggame.repository;

import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.entity.QuestionType;
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

    @Query("SELECT q FROM Question q WHERE q.date != :date ORDER BY :date DESC")
    List<Question> findAllByDateNotToday(@Param("date") LocalDate date);

    @Query("SELECT q FROM Question q WHERE q.id > :cursor AND q.id <= :nextCursor  AND q.date = :date AND q.questionType = :questionType ORDER BY q.id ASC")
    List<Question> findAllByDateAndQTypeByCursor(@Param("date") LocalDate date,
                                                 @Param("questionType") String questionType,
                                                 @Param("cursor") Long cursor,
                                                 @Param("nextCursor") Long nextCursor);
    @Query("SELECT q FROM Question q WHERE q.id > :cursor AND q.id <= :nextCursor  AND q.date = :date  ORDER BY q.id ASC")
    List<Question> findAllByDateByCursor(@Param("date") LocalDate date,
                                          @Param("cursor") Long cursor,
                                         @Param("nextCursor") Long nextCursor);

    @Query("SELECT q FROM Question q WHERE q.id > :cursor AND q.id <= :nextCursor  AND q.questionType = :questionType  ORDER BY q.id ASC")
    List<Question> findAllByQTypeByCursor(@Param("questionType") QuestionType questionType,
                                          @Param("cursor") Long cursor,
                                          @Param("nextCursor") Long nextCursor);

    @Query("SELECT q FROM Question q WHERE q.id > :cursor AND q.id <= :nextCursor ORDER BY q.id ASC")
    List<Question> findAllByCursor(@Param("cursor") Long cursor,@Param("nextCursor") Long nextCursor);


}
