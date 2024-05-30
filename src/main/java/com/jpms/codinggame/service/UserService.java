package com.jpms.codinggame.service;

import com.jpms.codinggame.entity.Role;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.SignupRequestDto;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //회원가입 로직
    public void signUp(SignupRequestDto signupRequestDto) {
        //이메일 중복 확인
        Optional<User> optionalUser = userRepository.findByEmail(signupRequestDto.getEmail());
//        if (optionalUser.isPresent()) throw new CustomEx();

        userRepository.save(User.builder()
                .userName(signupRequestDto.getUsername())
                .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()))
                .nickName(signupRequestDto.getNickName())
                .email(signupRequestDto.getEmail())
                .address(signupRequestDto.getAddress())
                .role(Role.ROLE_USER)
                .build());
    }


}
