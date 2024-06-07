package com.jpms.codinggame.service;

import com.jpms.codinggame.entity.Role;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.*;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final TempServerStorage tempServerStorage;
    private final EmailService emailService;


    //회원가입 로직
    public void signUp(SignupRequestDto signupRequestDto) throws Exception {
        //이메일 중복 확인
        Optional<User> optionalUser = userRepository.findByEmail(signupRequestDto.getEmail());
        if (optionalUser.isPresent()) throw new Exception();

        //닉네임 중복 확인
        Optional<User> optionalUser1 = userRepository.findByUserName(signupRequestDto.getUsername());
        if (optionalUser1.isPresent()) throw new Exception();

        // 비밀번호와 비밀번호 확인이 모두 일치하는지
        if(!passwordCheck(signupRequestDto.getPassword(), signupRequestDto.getCheckPassword())) throw new Exception();

        // 이메일 인증 로직
        int savedAuthNum = tempServerStorage.getVerificationCode(signupRequestDto.getEmail());
        if(signupRequestDto.getInputAuthNum() != savedAuthNum) throw new Exception();


        userRepository.save(User.builder()
                .userName(signupRequestDto.getUsername())
                .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()))
                .nickName(signupRequestDto.getNickName())
                .email(signupRequestDto.getEmail())
                .address(signupRequestDto.getAddress())
                .role(Role.ROLE_USER)
                .build());
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws Exception {

        Optional<User> optionalUser = userRepository.findByUserName(loginRequestDto.getUsername());
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

    // 아이디 찾기
    public void findAccountName(FindUserNameDto findAccountNameDto) throws Exception{
        // 회원 존재 여부 확인
        Optional<User> optionalUser = userRepository.findByEmail(findAccountNameDto.getEmail());
        if (optionalUser.isEmpty()) throw new Exception();

        // 이메일 발송
        emailService.sendFindAccountEmail(optionalUser.get().getEmail(), optionalUser.get().getUserName());

    }

    // 비밀번호 찾기
    @Transactional
    public void findPassword(FindPasswordDto findPasswordDto) throws Exception{
        // 회원 존재 여부 확인
        Optional<User> optionalUser = userRepository.findByUserName(findPasswordDto.getUserName());
        if (optionalUser.isEmpty()) throw new Exception();
        User user = optionalUser.get();

        //이메일 일치 확인
        if(!findPasswordDto.getEmail().matches(user.getEmail())) throw new Exception();
        String email = user.getEmail();

        // 임시 비밀번호 생성
        String tempPassword =  generateTempPassword();

        // 비밀번호 변경
        user.updateTempPassword(tempPassword);

        // 이메일 발송
        emailService.sendFindPasswordEmail(email, tempPassword);

    }

    // 임시 비밀번호 생성 로직
    public String generateTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        StringBuilder tempPassword = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int idx = (int) (charSet.length * Math.random());
            tempPassword.append(charSet[idx]);
        }
        return String.valueOf(tempPassword);
    }

    // 회원정보 수정
    @Transactional
    public void updateUser(
            UpdateUserInfoDto dto,
            Authentication authentication
    ){
        Optional<User> optionalUser = userRepository.findById((Long) authentication.getPrincipal());
        if(optionalUser.isEmpty()){
            throw new RuntimeException();
        }
        User user = optionalUser.get();
        user.updateInfo(dto.getPassword(), dto.getNickName(), dto.getAddress());

    }

    public boolean passwordCheck(String password, String checkPassword) {
        if (password == null || checkPassword == null) {
            return false;
        }
        return password.equals(checkPassword);
    }


    public UserInfoDto getUserInfo(Authentication authentication) throws Exception {
        Optional<User> optionalUser = userRepository.findById((Long) authentication.getPrincipal());
        if(optionalUser.isEmpty()){
            throw new RuntimeException();
        }
        User user = optionalUser.get();

        return UserInfoDto.builder()
                .userName(user.getUserName())
                .nickName(user.getNickName())
                .score(user.getTotalScore())
                .tier(user.getTier())
                .address(user.getAddress())
                .build();
    }
}
