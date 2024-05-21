package com.jpms.codinggame.repository;

import com.jpms.codinggame.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT COALESCE(MAX(q.questionNo), 0) FROM Question q WHERE q.date = :date")
    int findMaxQuestionNoByDate(@Param("date") LocalDate date);
}
