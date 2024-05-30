package com.jpms.codinggame.service;

import com.jpms.codinggame.entity.Role;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.LoginRequestDto;
import com.jpms.codinggame.global.dto.LoginResponseDto;
import com.jpms.codinggame.global.dto.SignupRequestDto;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

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

    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws Exception {

        Optional<User> optionalUser = userRepository.findByEmail(loginRequestDto.getEmail());
        if (optionalUser.isEmpty()) throw new Exception();
        else if (!bCryptPasswordEncoder.matches(loginRequestDto.getPassword(), optionalUser.get().getPassword()))
            throw new Exception();

        User user = optionalUser.get();

        String accessToken = jwtTokenUtil.createToken(user.getId(),"access");
        String refreshToken = jwtTokenUtil.createToken(user.getId(),"refresh");

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }



}
