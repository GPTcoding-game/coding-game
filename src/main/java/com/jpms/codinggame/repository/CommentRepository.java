package com.jpms.codinggame.repository;

import com.jpms.codinggame.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByQnaId(Long qna_id);
}
