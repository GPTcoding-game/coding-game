package com.jpms.codinggame.encrpytion;

import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AESUtil {




    @Value("${aes.secret}")
    private String aesKey;

//    private final UserService userService;
//
//    public AESUtil(UserService userService) {
//        this.userService = userService;
//    }

    public String encrypt(long id) throws CustomException {
        try {
            // 암호화 키 생성
            SecretKey secretKey = new SecretKeySpec(aesKey.getBytes(), "AES");

            // 암호화를 위한 사이퍼 인스턴스
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // 인풋값을 바이트로 변환 후 암호화
            byte[] idBytes = String.valueOf(id).getBytes();
            byte[] encryptedBytes = cipher.doFinal(idBytes);

            // 암호화된 바이트 배열을 문자열로 반환
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ENCRYPTION_FAILED);
        }
    }

    public long decrypt(String encryptedId, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws CustomException {
        try {
            // 복호화 키 생성
            SecretKey secretKey = new SecretKeySpec(aesKey.getBytes(), "AES");

            // 복호화를 위한 사이퍼 인스턴스
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // 인풋값을 디코딩하여 바이트 배열로 변환 후 복호화
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedId);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 복호화된 바이트 배열을 long으로 변환
            String decryptedId = new String(decryptedBytes);
            return Long.parseLong(decryptedId);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.DECRYPTION_FAILED);
        }
    }
}
