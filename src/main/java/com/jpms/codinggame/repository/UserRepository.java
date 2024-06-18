package com.jpms.codinggame.repository;

import com.jpms.codinggame.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);


    Optional<User> findByUserName(String username);

    List<User> findTop50ByOrderByTotalScoreDesc();

    @Modifying
    @Query("UPDATE User u SET u.isDone = true WHERE u.id = :id")
    @Transactional
    void updateState(@Param("id") Long id);

    /*
    * 누적 순위 계산 쿼리
    * */
    @Query("SELECT COUNT(u)+1 FROM User u WHERE u.totalScore > :totalScore")
    int findRankByTotalScore(@Param("totalScore") int totalScore);

    Optional<User> findByNickName(String nickName);
}
