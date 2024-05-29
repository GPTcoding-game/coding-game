package com.jpms.codinggame.repository;

import com.jpms.codinggame.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    User findByUserName(String userName);
    List<User> findTop50ByOrderByScoreDesc();
}
