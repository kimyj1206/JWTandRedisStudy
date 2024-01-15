package project.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import project.config.JwtProperties;
import project.domain.Token;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private final StringRedisTemplate stringRedisTemplate;

    public static String createAccessToken(String email, String secret, Long expiredMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", email);

        //long expirationTime = System.currentTimeMillis() + (30 * 60 * 1000);
        long expirationTime = System.currentTimeMillis() + (3 * 60 * 1000); // 테스트를 위해 3분 지정

        return Jwts.builder()
                .setHeaderParam("typ", "jwt")
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationTime)) // 만료 시간 30분 지정
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


    public static String createRefreshToken(String secret) {
        // 리프레시 토큰은 사용자 정보를 담을 필요 없음
        Claims claims = Jwts.claims();

        return Jwts.builder()
                .setHeaderParam("typ", "jwt")
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3)) // 리프레시 토큰 만료 기간은 3일로 설정
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
