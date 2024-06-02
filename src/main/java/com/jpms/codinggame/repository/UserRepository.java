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

    List<User> findTop50ByOrderByScoreDesc();

    @Modifying
    @Query("UPDATE User u SET u.isDone = true WHERE u.id = :id")
    @Transactional
    void updateState(@Param("id") Long id);
}
