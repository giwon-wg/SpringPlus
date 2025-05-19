package com.example.springplusteamproject.jwt;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${spring.jwt.secret}")
    private String secretKey;
    private static final long EXPIRATION_TIME = 60 * 60 * 1000L; // 60분

    public String generateToken(User loginUser) {
        return BEARER_PREFIX +
                Jwts.builder()
                .setSubject(loginUser.getId().toString())
                .claim("email", loginUser.getEmail())
                .claim("userRole", loginUser.getUserRole())
                .claim("nickname", loginUser.getNickname())
                .setIssuedAt(new Date()) // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 토큰 만료 시간
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // 서명
                .compact(); // JWT 문자열 생성
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(BEARER_PREFIX.length());
        }
        throw new ApiException(ErrorStatus.JWT_NOT_FOUND_TOKEN);
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT 만료됨: {}", token);
        } catch (io.jsonwebtoken.SignatureException e) {
            log.warn("JWT 서명 오류: {}", token);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.warn("JWT 형식 오류: {}", token);
        } catch (JwtException e) {
            log.warn("기타 JWT 오류: {}", token);
        }
        return null;
    }

    @PostConstruct
    public void checkSecretKey() {
        System.out.println("JwtUtil Loaded Secret Key: " + secretKey);
    }
}
