package com.jpms.codinggame.SDKSocialLogin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.global.dto.SessionDataDto;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

@Service
public class SDKService {

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    // 구글 토큰 검증
    public GoogleIdToken.Payload verifyGoogleToken(String idTokenString) throws GeneralSecurityException, IOException {

        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            return null;
        }
    }

    public JsonNode verifyKakaoToken(String accessToken) throws IOException, InterruptedException {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Authorization", "Bearer " + accessToken)
                .header("KakaoAK", kakaoClientId) // 앱 키를 헤더에 포함
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body());
    }

    public Map<String, Object> verifyAppleToken(String idToken) throws Exception {
        SignedJWT signedJWT = AppleUtils.verifyAppleToken(idToken);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        return claims.getClaims();
    }


    public boolean checkCompulsoryField(User user) {
        return user.getNickName() != null && !user.getNickName().isEmpty() &&
                user.getAddress() != null && !user.getAddress().isEmpty();
    }


    public void validateSessionData(HttpSession session) {
        String email = (String) session.getAttribute("email");
        String username = (String) session.getAttribute("username");
        String provider = (String) session.getAttribute("provider");

        if (email == null || username == null || provider == null) {
            throw new CustomException(ErrorCode.INVALID_SESSION);
        }
    }

    public SessionDataDto getSessionData(HttpSession session) {
        validateSessionData(session); // 중복 검증을 피하기 위해 재사용
        String email = (String) session.getAttribute("email");
        String username = (String) session.getAttribute("username");
        String provider = (String) session.getAttribute("provider");

        return new SessionDataDto(email, username, provider);
    }


}
