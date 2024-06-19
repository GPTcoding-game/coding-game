package com.jpms.codinggame.service;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.util.Optional;

import static com.jpms.codinggame.exception.ErrorCode.EXISTING_EMAIL_EXCEPTION;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail= "gptcodinggame@gmail.com";
    private static int number;
    private final SubRedisService subRedisService;
    private final UserRepository userRepository;

    // 랜덤 인증 코드 생성
    public static void createNumber() {
        number = (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    // 인증번호 메일 양식
    public MimeMessage createSignupMail(String mail){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("[앱 이름] 회원가입을 위한 이메일 인증");
            String body = "";
            body += "<h1>" + "안녕하세요! [앱 이름] 입니다." + "</h1>";
            body += "<h3>" + "회원가입을 위한 인증 번호입니다." + "</h3><br>";
            body += "<h2>" + "아래 코드를 회원가입 창에 입력해주세요." + "</h2>";
            body += "<div align='center' style='border:1px solid black; font-family:verdana;'>";
            body += "<h2>" + "인증 코드" + "</h2>";
            body += "<h1 style='color:blue'>" + number + "</h1>";
            body += "</div><br>";
            body += "<h3>" + "감사합니다" + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    // 인증번호 메일 전송
    public int sendSignupEmail(String email) throws CustomException {
        // 이메일 중복 확인
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) throw new CustomException(ErrorCode.EXISTING_EMAIL_EXCEPTION);
        // 이메일 발송
        MimeMessage message = createSignupMail(email);
        javaMailSender.send(message);
        subRedisService.setValue(email,String.valueOf(number));

        // 인증 코드 전달
        return number;
    }

    // 아이디 찾기 이메일 양식
    public MimeMessage createFindAccountMail(String mail, String userName){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);   // 보내는 이메일
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("[앱 이름] 아이디 찾기");
            String body = "";
            body += "<h1>" + "안녕하세요! [앱 이름] 입니다." + "</h1>";
            body += "<h3>" + "귀하의 아이디 입니다" + "</h3><br>";
            body += "<div align='center' style='border:1px solid black; font-family:verdana;'>";
            body += "<h2>" + "귀하께서는 현재" + "</h2>";
            body += "<h1 style='color:blue'>" + userName + "</h1>";
            body += "<h2>" + "으로 등록되어 있습니다." + "</h2>";
            body += "</div><br>";
            body += "<h3>" + "감사합니다" + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    // 아이디 찾기 메일 전송
    public void sendFindAccountEmail(String email, String userName){
        MimeMessage message = createFindAccountMail(email, userName);
        javaMailSender.send(message);
    }

    // 임시 비밀번호 발급 메일 양식
    public MimeMessage createTempPasswordMail(String mail, String tempPassword){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);   // 보내는 이메일
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("[앱 이름] 에서 요청하신 임시 비밀번호");
            String body = "";
            body += "<h1>" + "안녕하세요! [앱 이름] 입니다." + "</h1>";
            body += "<h3>" + "귀하의 요청에 따라 아래의 임시 비밀번호를 발급 드립니다" + "</h3><br>";
            body += "<div align='center' style='border:1px solid black; font-family:verdana;'>";
            body += "<h2>" + "귀하의 임시 비밀번호" + "</h2>";
            body += "<h1 style='color:blue'>" +  tempPassword + "</h1>";
            body += "</div><br>";
            body += "<h3>" + "해당 비밀번호로 로그인 하신 후 반드시 비밀번호를 변경해 주십시오." + "</h3>";
            body += "<h3>" + "감사합니다" + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    // 임시 비밀번호 발급 메일 전송
    public void sendFindPasswordEmail(String email, String tempPassword){
        MimeMessage message = createTempPasswordMail(email,tempPassword);
        javaMailSender.send(message);
    }

}
