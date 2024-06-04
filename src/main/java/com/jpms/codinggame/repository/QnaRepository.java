package com.jpms.codinggame.repository;

import com.jpms.codinggame.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {
    List<Qna> findAllByQuestionId(Long question_id);
    List<Qna> findTop5ByOrderByIdDesc();
}
