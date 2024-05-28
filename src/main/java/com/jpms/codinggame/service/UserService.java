package com.jpms.codinggame.service;

import com.jpms.codinggame.dto.LogInDto;
import com.jpms.codinggame.entity.JwtToken;
import com.jpms.codinggame.entity.Role;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.SignupRequestDto;
import com.jpms.codinggame.jwt.JwtTokenProvider;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입 로직
    public void signUp(SignupRequestDto signupRequestDto) throws Exception {
        //이메일 중복 확인
        Optional<User> optionalUser = userRepository.findByEmail(signupRequestDto.getEmail());
        if (optionalUser.isPresent()) throw new Exception();

        //닉네임 중복 확인
        Optional<User> optionalUser1 = userRepository.findByUserName(signupRequestDto.getUsername());
        if (optionalUser1.isPresent()) throw new Exception();

        userRepository.save(User.builder()
                .userName(signupRequestDto.getUsername())
                .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()))
                .email(signupRequestDto.getEmail())
                .address(signupRequestDto.getAddress())
                .role(Role.ROLE_USER)
                .build());
    }

    public JwtToken LogIn(LogInDto logInDto) throws Exception {
        Optional<User> optionalUser = userRepository.findByUserName(logInDto.getUsername());
        if (optionalUser.isEmpty()) throw new Exception();
        else if (!bCryptPasswordEncoder.matches(logInDto.getPassword(), optionalUser.get().getPassword()))
            throw new Exception();


    }


}
