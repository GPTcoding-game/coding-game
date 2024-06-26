package com.jpms.codinggame.repository;

import com.jpms.codinggame.entity.Qna;
import com.jpms.codinggame.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {
    List<Qna> findAllByQuestionId(Long question_id);
    List<Qna> findAllByUser(User user);
    List<Qna> findTop5ByOrderByIdDesc();
}
