package project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import project.domain.Token;
import project.utils.JwtUtil;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${spring.jwt.secret}")
    private String secret; // application.yml의 비밀키를 가져옴

    private Long expiredMs = 1000 * 60 * 60L; // 1시간

    public boolean insertToken(Token token) {
        try {
            String resultToken = JwtUtil.createToken(token.getEmail(), secret, expiredMs);// 토큰 생성

            ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
            stringValueOperations.set(token.getEmail(), resultToken); // redis 저장

            return true;
        }catch (Exception e) {
            System.out.println(e.getMessage());

            return false;
        }
    }

    /*public String selectUserByToken(String token, String secret) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
                    .getBody().get("email", String.class);

        }catch (Exception e) {
            return e.getMessage();
        }
    }*/

    public boolean updateToken(String token) {

        return true;
    }

    public boolean deleteToken(String token) {

        return true;
    }
}
