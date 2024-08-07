package com.jpms.codinggame.service;

import com.jpms.codinggame.Oauth2.PrincipalDetails;
import com.jpms.codinggame.dto.DeleteUserDto;
import com.jpms.codinggame.dto.MainInfoResDto;
import com.jpms.codinggame.entity.Role;
import com.jpms.codinggame.entity.Tier;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.exception.ValidationErrorCode;
import com.jpms.codinggame.exception.ValidationException;
import com.jpms.codinggame.global.dto.*;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmailService emailService;
    private final RedisService redisService;
    private final RankService rankService;
    private final CookieUtil cookieUtil;
    private final SubRedisService subRedisService;



    //회원가입 로직
    public void signUp(SignupRequestDto signupRequestDto) throws ValidationException {
        List<ValidationErrorCode> errorCodes = new ArrayList<>();

        // 아이디 중복 확인
        Optional<User> optionalUser1 = userRepository.findByUserName(signupRequestDto.getUsername());
        if (optionalUser1.isPresent()) errorCodes.add(ValidationErrorCode.EXISTING_USERNAME_EXCEPTION);

        // 닉네임 중복 확인
        Optional<User> optionalUser2 = userRepository.findByNickName(signupRequestDto.getNickName());
        if (optionalUser2.isPresent()) errorCodes.add(ValidationErrorCode.EXISTING_NICKNAME_EXCEPTION);

        // 비밀번호와 비밀번호 확인이 모두 일치하는지 >> 비밀번호 >> 프론트에서 하는걸로 합의
//        if (!passwordCheck(signupRequestDto.getPassword(), signupRequestDto.getCheckPassword()))
//            errorCodes.add(ErrorCode.PASSWORD_CHECK_FAILED);

        // 이메일 인증 로직
        // 이메일이 없거나 redis에 없는 경우도 상정해야함
        if(subRedisService.getValue(signupRequestDto.getEmail()) == null){
            errorCodes.add(ValidationErrorCode.EMAIL_VERIFICATION_FAILED);
        }else{
            int savedAuthNum = Integer.parseInt(subRedisService.getValue(signupRequestDto.getEmail()));
            if (signupRequestDto.getInputAuthNum() != savedAuthNum) {
                errorCodes.add(ValidationErrorCode.EMAIL_VERIFICATION_FAILED);
            }
        }


        // 예외가 하나라도 있으면 ValidationException 던지기
        if (!errorCodes.isEmpty()) {
            throw new ValidationException(errorCodes);
        }



        // 예외가 없으면 유저 저장
        userRepository.save(User.builder()
                .userName(signupRequestDto.getUsername())
                .nickName(signupRequestDto.getNickName())
                .tier(Tier.BRONZE)
                .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()))
                .email(signupRequestDto.getEmail())
                .totalScore(0)
                .isDone(true)
                .role(Role.ROLE_USER)
                .address(signupRequestDto.getAddress())
                .provider("CodingGame")
                .picture("https://avatar.iran.liara.run/username?username=" + signupRequestDto.getUsername())
                .isDeleted(false)
                .build());
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws CustomException {

        Optional<User> optionalUser = userRepository.findByUserName(loginRequestDto.getUsername());
        if (optionalUser.isEmpty()) throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
        else if (!bCryptPasswordEncoder.matches(loginRequestDto.getPassword(), optionalUser.get().getPassword()))
            throw new CustomException(ErrorCode.PASSWORD_INVALID);

        User user = optionalUser.get();
        Long userId = user.getId();

        String accessToken = jwtTokenUtil.createToken(userId,"access");
        String refreshToken = jwtTokenUtil.createToken(userId,"refresh");

        redisService.put(String.valueOf(userId), "refreshToken", refreshToken);

        SecurityContextHolder.getContext().setAuthentication(jwtTokenUtil.getAuthentication(userId));

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    // 아이디 찾기
    public void findAccountName(FindUserNameDto findAccountNameDto) throws CustomException{
        // 회원 존재 여부 확인
        Optional<User> optionalUser = userRepository.findByEmail(findAccountNameDto.getEmail());
        if (optionalUser.isEmpty()) throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);

        // 이메일 발송
        emailService.sendFindAccountEmail(optionalUser.get().getEmail(), optionalUser.get().getUserName());

    }

    // 비밀번호 찾기
    @Transactional
    public void findPassword(FindPasswordDto findPasswordDto) throws CustomException{
        // 회원 존재 여부 확인
        Optional<User> optionalUser = userRepository.findByUserName(findPasswordDto.getUserName());
        if (optionalUser.isEmpty()) throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
        User user = optionalUser.get();

        //이메일 일치 확인
        if(!findPasswordDto.getEmail().matches(user.getEmail())) throw new CustomException(ErrorCode.EMAIL_MISMATCH_EXCEPTION);
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
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        Optional<User> optionalUser = userRepository.findByNickName(dto.getNickName());
        if(optionalUser.isPresent()){throw new CustomException(ErrorCode.EXISTING_NICKNAME_EXCEPTION);}


        user.updateInfo(bCryptPasswordEncoder.encode(dto.getPassword()), dto.getNickName(), dto.getAddress());
        userRepository.save(user);

        //객체 필드값에 넣고 save 해줘야 저장됨
        userRepository.save(user);

    }

    public boolean passwordCheck(String password, String checkPassword) {
        if (password == null || checkPassword == null) {
            return false;
        }
        return password.equals(checkPassword);
    }


    public UserInfoDto getUserInfo(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        // 소셜로그인 사용자의 경우 계정이름이 아닌 제공자+"계정 사용자"의 형식으로 반환\
        String username = user.getUserName();
        if(!user.getProvider().equals("CodingGame")){
            username = user.getProvider()+"계정 사용자";
        }

        return UserInfoDto.builder()
                .userName(username)
                .nickName(user.getNickName())
                .totalScore(user.getTotalScore())
                .tier(user.getTier())
                .address(user.getAddress())
                .todayRank(rankService.getMyTodayRank(user))
                .picture(user.getPicture())
                .build();
    }

    /*
    * 메인화면 유저 정보 가져오기
    * */
    public MainInfoResDto getMainInfo(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        String todayRank =  String.valueOf(rankService.getMyTodayRank(user));
        String allDayRank = String.valueOf(rankService.getMyAllDayRank(user));

        if(todayRank.equals("0")) todayRank = "점수 없음";

        return MainInfoResDto
                .builder()
                .nickName(user.getNickName())
                .tier(user.getTier().toString())
                .todayRank(todayRank)
                .allDayRank(allDayRank)
                .possibleCount(String.valueOf(redisService.getPossibleCount(user)))
                .todayScore(String.valueOf(redisService.getTodayScore(user)))
                .pictureUrl(user.getPicture())
                .build();
    }

    /*
    * Redis 에 데이터 생성되었는지 ( 오늘 처음 입장한 것인지 확인 )
    * */
    public void firstEntrance(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        if(!redisService.hasKey(String.valueOf(user.getId()),"score")){
            setRedisData(user);
        }
    }

    /*
     * 최초 게임시작 시 redis data 세팅 메서드
     * */
    public void setRedisData(User user){
        //REDIS 데이터 생성

        //Key : userId , HashKey : "username" , Value : username
        redisService.put(String.valueOf(user.getId()),"nickname",user.getNickName());
        //Key : userId , HashKey : "score" , Value : score
        redisService.put(String.valueOf(user.getId()),"score",0);
        //Key : userId, HashKey : "possibleCount", Value : count (최초 3)
        redisService.put(String.valueOf(user.getId()),"possibleCount",3);
    }

    public void addOauthUserInfo(NicknameAddressDto addInfoDto, User user) throws CustomException {
        Optional<User> optionalUser = userRepository.findByNickName(addInfoDto.getNickName());
        if(optionalUser.isPresent()){throw new CustomException(ErrorCode.EXISTING_NICKNAME_EXCEPTION);}
        if (addInfoDto.getNickName().isEmpty() || addInfoDto.getNickName() == null){
            throw new CustomException(ErrorCode.EMPTY_NICKNAME_EXCEPTION);
        }
        user.addInfo(addInfoDto.getNickName(), addInfoDto.getAddress());
        userRepository.save(user);
    };

    public GetInfoResponseDto getCompulsoryInfo(User user){
        return GetInfoResponseDto.builder()
                .email(user.getEmail())
                .nickName(user.getNickName())
                .address(user.getAddress())
                .build();
    }


//    public void logOut(Authentication authentication, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        long userId = principalDetails.getId();
//
//        // 리프레시 토큰 삭제
//        redisService.delete(String.valueOf(userId), "refreshToken");
//
//        // 세션 무효화
//        session.invalidate();
//
//        // 리프레시 토큰 쿠키 삭제
//        cookieUtil.deleteCookie(request, response, "refreshToken");
//    }
    public void logOut(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Authentication이 null이 아닌 경우 Redis에서 토큰 삭제
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            long userId = principalDetails.getId();

            // 리프레시 토큰 삭제
            redisService.delete(String.valueOf(userId), "refreshToken");
        }

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        // 세션 무효화
        request.getSession().invalidate();

        // 리프레시 토큰 쿠키 삭제
        cookieUtil.deleteCookie(request, response, "refreshToken");

    }


    public void deleteUser(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Long userId = principalDetails.getUser().getId();
        userRepository.deleteById(userId);
    }

    public void deleteUserWithSwagger(DeleteUserDto deleteUserDto){
        log.info(deleteUserDto.getEmail());
        Optional<User> optionalUser = userRepository.findByEmail(deleteUserDto.getEmail());
        User user = optionalUser.get();
        Long userId = user.getId();
        userRepository.deleteById(userId);

//        user.deleteUser(true);
//        userRepository.save(user);
    }


    public Long socialSignup(String email, String name) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {

            user = User.builder()
                    .userName(name)
                    .nickName(null)
                    .tier(Tier.BRONZE)
                    .password(null)
                    .email(email)
                    .totalScore(0)
                    .isDone(true)
                    .role(Role.ROLE_USER)
                    .address(null)
                    .build();
            userRepository.save(user);
        }
        return user.getId();
    }





}
