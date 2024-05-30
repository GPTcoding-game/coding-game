package com.jpms.codinggame.jwt;

import com.jpms.codinggame.encrpytion.AESUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtil {
    private final Key signingKey;
    private final JwtParser jwtParser;

    private final AESUtil aesUtil;

    public static long accessTokenDuration = 1000 * 60 * 30  ;
    public static long refreshTokenDuration = 1000 * 60 * 60 * 3;

    public JwtTokenUtil(@Value("${jwt.secret}") String secretKey, AESUtil aesUtil){
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.aesUtil = aesUtil;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(signingKey).build();
    }

    //토큰 생성
    public String createToken(long userId, String type) throws Exception {

        long duration = 0;

        //유연성을 위하여 처음 생성시 기본 설정만 정의
        Claims claims = Jwts.claims().setSubject(aesUtil.encrypt(userId));
        if (type.equals("access")){
            claims.put("type","access");
            duration = accessTokenDuration;
        }
        if(type.equals("refresh")){
            claims.put("type","refresh");
            duration = refreshTokenDuration;
        }
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + duration))
                .signWith(signingKey)
                .compact();

    }


    public boolean validateToken(String token){
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e){
            log.warn("invalid jwt: {}", e.getClass());
            return false;
        } catch (Exception e){
            log.warn("invalid jwt: {}", e.getClass());
            return false;
        }
    }

    // 토큰을 파싱하여 암호화된 id 추출 후 복호화
    public long getId(String token) throws Exception {
        return aesUtil.decrypt(jwtParser.parseClaimsJws(token).getBody().getSubject());
    }

    public Authentication getAuthentication(long userId) {
        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }




}
